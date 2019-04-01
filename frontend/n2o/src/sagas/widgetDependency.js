import {
  select,
  fork,
  actionChannel,
  take,
  call,
  put,
} from 'redux-saga/effects';
import { keys, isEqual, reduce } from 'lodash';
import { REGISTER_DEPENDENCY } from '../constants/dependency';
import { SET } from '../constants/models';
import { DEPENDENCY_TYPES } from '../core/dependencyTypes';
import {
  dataRequestWidget,
  showWidget,
  hideWidget,
  enableWidget,
  disableWidget,
} from '../actions/widgets';
import { getModelsByDependency } from '../selectors/models';
import { makeWidgetVisibleSelector } from '../selectors/widgets';
import propsResolver from '../utils/propsResolver';

export const reduceFunction = (isTrue, { model, config }) => {
  return isTrue && propsResolver('`' + config.condition + '`', model);
};

/**
 * Наблюдение за регистрацией зависимостей виджетов и изменением модели
 * @returns {IterableIterator<*>}
 */
export function* watchDependency() {
  let widgetsDependencies = {};
  const channel = yield actionChannel([REGISTER_DEPENDENCY, SET]);
  while (true) {
    const prevState = yield select();
    const action = yield take(channel);
    const { type, payload } = action;
    const { widgetId, dependency, key } = payload;
    const state = yield select();
    switch (type) {
      case REGISTER_DEPENDENCY: {
        widgetsDependencies = yield call(
          registerWidgetDependency,
          widgetsDependencies,
          widgetId,
          dependency
        );
        yield fork(
          resolveWidgetDependency,
          prevState,
          state,
          widgetsDependencies,
          widgetId
        );
        break;
      }
      case SET: {
        yield fork(
          resolveWidgetDependency,
          prevState,
          state,
          widgetsDependencies,
          key
        );
        break;
      }
      default:
        break;
    }
  }
}

/**
 * Добавляет в хранилище новопришедший виджет с его widgetId и dependency
 * @param widgetsDependencies
 * @param widgetId
 * @param dependency
 * @returns {{}}
 */
export function* registerWidgetDependency(
  widgetsDependencies,
  widgetId,
  dependency
) {
  return {
    ...widgetsDependencies,
    [widgetId]: {
      widgetId,
      dependency,
    },
  };
}

/**
 * Резолв всех зависимостей виджета
 * @param prevState
 * @param state
 * @param widgetsDependencies
 * @returns {IterableIterator<*|CallEffect>}
 */
export function* resolveWidgetDependency(
  prevState,
  state,
  widgetsDependencies
) {
  const dependenciesKeys = keys(widgetsDependencies);
  for (let i = 0; i < dependenciesKeys.length; i++) {
    const { dependency, widgetId } = widgetsDependencies[dependenciesKeys[i]];
    const widgetDependenciesKeys = keys(dependency);
    const isVisible = makeWidgetVisibleSelector(widgetId)(state);
    for (let j = 0; j < widgetDependenciesKeys.length; j++) {
      const prevModel = getModelsByDependency(
        dependency[widgetDependenciesKeys[j]]
      )(prevState);
      const model = getModelsByDependency(
        dependency[widgetDependenciesKeys[j]]
      )(state);
      if (!isEqual(prevModel, model)) {
        yield fork(
          resolveDependency,
          widgetDependenciesKeys[j],
          widgetId,
          model,
          isVisible
        );
      }
    }
  }
}

/**
 * Резолв конкретной зависимости по типу
 * @param dependencyType
 * @param widgetId
 * @param model
 * @param isVisible
 * @returns {IterableIterator<*|CallEffect>}
 */
export function* resolveDependency(dependencyType, widgetId, model, isVisible) {
  switch (dependencyType) {
    case DEPENDENCY_TYPES.fetch: {
      if (isVisible) {
        yield call(resolveFetchDependency, widgetId);
      }
      break;
    }
    case DEPENDENCY_TYPES.visible: {
      yield call(resolveVisibleDependency, widgetId, model);
      break;
    }
    case DEPENDENCY_TYPES.enabled: {
      yield call(resolveEnabledDependency, widgetId, model);
      break;
    }
    default:
      break;
  }
}

/**
 * Резолв запросов
 * @param widgetId
 * @returns {IterableIterator<*>}
 */
export function* resolveFetchDependency(widgetId) {
  yield put(dataRequestWidget(widgetId));
}

/**
 * Резолв видимости
 * @param widgetId
 * @param model
 * @returns {IterableIterator<*>}
 */
export function* resolveVisibleDependency(widgetId, model) {
  const visible = reduce(model, reduceFunction, true);
  if (visible) {
    yield put(showWidget(widgetId));
  } else {
    yield put(hideWidget(widgetId));
  }
}

/**
 * Резолв активности
 * @param widgetId
 * @param model
 * @returns {IterableIterator<*>}
 */
export function* resolveEnabledDependency(widgetId, model) {
  const enabled = reduce(model, reduceFunction, true);
  if (enabled) {
    yield put(enableWidget(widgetId));
  } else {
    yield put(disableWidget(widgetId));
  }
}

export const widgetDependencySagas = [fork(watchDependency)];