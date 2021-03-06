import {
  call,
  put,
  select,
  take,
  throttle,
  fork,
  takeEvery,
  delay,
  cancel,
} from 'redux-saga/effects';
import isEmpty from 'lodash/isEmpty';
import isUndefined from 'lodash/isUndefined';
import some from 'lodash/some';
import includes from 'lodash/includes';
import get from 'lodash/get';
import isEqual from 'lodash/isEqual';
import set from 'lodash/set';
import { actionTypes, change, getFormValues } from 'redux-form';
import evalExpression from '../utils/evalExpression';

import { makeFormByName } from '../selectors/formPlugin';
import { REGISTER_FIELD_EXTRA } from '../constants/formPlugin';
import {
  makeFormModelPrefixSelector,
  makeWidgetValidationSelector,
} from '../selectors/widgets';
import { validateField } from '../core/validation/createValidator';
import {
  enableField,
  disableField,
  showField,
  hideField,
  setRequired,
  unsetRequired,
  setLoading,
} from '../actions/formPlugin';
import { FETCH_VALUE } from '../core/api';
import fetchSaga from './fetch';
import { dataProviderResolver } from '../core/dataProviderResolver';
import { evalResultCheck } from '../utils/evalResultCheck';
import { setModel } from '../actions/models';

let prevState = {};
let prevResults = {};

export function* fetchValue(form, field, { dataProvider, valueFieldId }) {
  try {
    yield delay(300);
    yield put(setLoading(form, field, true));
    const state = yield select();
    const { url, headersParams } = yield call(
      dataProviderResolver,
      state,
      dataProvider
    );
    const response = yield call(fetchSaga, FETCH_VALUE, {
      url,
      headers: headersParams,
    });
    const model = get(response, 'list[0]', null);

    if (model) {
      yield put(
        change(form, field, {
          keepDirty: false,
          value: valueFieldId ? model[valueFieldId] : model,
        })
      );
    }
  } catch (e) {
    throw e;
  } finally {
    yield put(setLoading(form, field, false));
  }
}

export function* modify(
  values,
  formName,
  fieldName,
  type,
  options = {},
  dispatch
) {
  let _evalResult;

  const prevValues = get(prevState, [formName, fieldName, type]);
  const prevResult = get(prevResults, [formName, fieldName, type]);

  if (prevValues && isEqual(prevValues, values)) return;

  if (options.expression) {
    _evalResult = evalExpression(options.expression, values);
  }

  if (!isUndefined(prevResult) && isEqual(_evalResult, prevResult)) return;

  set(prevResults, [formName, fieldName, type], _evalResult);
  set(prevState, [formName, fieldName, type], values);

  switch (type) {
    case 'enabled':
      yield _evalResult
        ? put(enableField(formName, fieldName))
        : put(disableField(formName, fieldName));
      break;
    case 'visible':
      yield _evalResult
        ? put(showField(formName, fieldName))
        : put(hideField(formName, fieldName));
      break;
    case 'setValue':
      let newFormValues = Object.assign({}, values);

      if (!isUndefined(_evalResult)) {
        const modelPrefix = yield select(makeFormModelPrefixSelector(formName));
        newFormValues = Object.assign({}, values);
        set(newFormValues, fieldName, _evalResult);

        yield put(setModel(modelPrefix || 'resolve', formName, newFormValues));
      }

      const state = yield select();
      const validation = makeWidgetValidationSelector(formName)(state);

      validateField(validation, formName, state)(newFormValues, dispatch);
      break;
    case 'reset':
      yield evalResultCheck(_evalResult) &&
        put(
          change(formName, fieldName, {
            keepDirty: false,
            value: null,
          })
        );
      break;
    case 'required':
      yield _evalResult
        ? put(setRequired(formName, fieldName))
        : put(unsetRequired(formName, fieldName));
      break;
    case 'fetchValue':
      const watcher = yield fork(fetchValue, formName, fieldName, options);
      const action = yield take(actionTypes.CHANGE);

      if (get(action, 'meta.field') !== fieldName) {
        yield cancel(watcher);
      }
      break;
    default:
      break;
  }
}

export function* checkAndModify(
  values,
  fields,
  formName,
  fieldName,
  actionType,
  dispatch
) {
  for (const entry of Object.entries(fields)) {
    const [fieldId, field] = entry;
    if (field.dependency) {
      for (const dep of field.dependency) {
        if (
          (fieldName && includes(dep.on, fieldName)) ||
          (fieldName &&
            some(
              dep.on,
              field => includes(field, '.') && includes(field, fieldName)
            )) ||
          ((actionType === actionTypes.INITIALIZE ||
            actionType === REGISTER_FIELD_EXTRA) &&
            dep.applyOnInit)
        ) {
          yield call(
            modify,
            values,
            formName,
            fieldId,
            dep.type,
            dep,
            dispatch
          );
        }
      }
    }
  }
}

export function* resolveDependency(action, dispatch) {
  try {
    const { form: formName, field: fieldName } = action.meta;
    const form = yield select(makeFormByName(formName));
    if (!isEmpty(form)) {
      const { values, registeredFields: fields } = form;
      if (!isEmpty(fields)) {
        yield call(
          checkAndModify,
          values,
          fields,
          formName,
          fieldName || action.name,
          action.type,
          dispatch
        );
      }
    }
  } catch (e) {
    // todo: падает тут из-за отсуствия формы
    console.error(e);
  }
}

export function* catchAction(dispatch) {
  const resolveDependencyHandler = action =>
    resolveDependency(action, dispatch);

  yield takeEvery(actionTypes.INITIALIZE, resolveDependencyHandler);
  yield throttle(300, REGISTER_FIELD_EXTRA, resolveDependencyHandler);
  yield takeEvery(actionTypes.CHANGE, resolveDependencyHandler);
}

export const fieldDependencySagas = [catchAction];
