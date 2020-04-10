import get from 'lodash/get';
import {
  INSERT,
  DESTROY,
  HIDE,
  SHOW,
  SHOW_PROMPT,
  HIDE_PROMPT,
} from '../constants/overlays';

const defaultState = {
  visible: false,
  name: null,
  showPrompt: false,
  mode: 'modal',
  props: {},
};

function resolve(state = defaultState, action) {
  switch (action.type) {
    case INSERT:
      const { visible, name, mode, ...props } = action.payload;
      return Object.assign({}, state, {
        visible,
        name,
        mode,
        props: Object.assign({}, props),
      });
    case SHOW:
      return Object.assign({}, state, {
        visible: true,
      });
    case HIDE:
      return Object.assign({}, state, {
        visible: false,
      });
    default:
      return state;
  }
}

/**
 * Редюсер экшенов оверлеев
 */
export default function overlays(state = [], action) {
  const index = state.findIndex(
    overlay => overlay.name === get(action, 'payload.name')
  );
  switch (action.type) {
    case INSERT:
      return [...state, resolve({}, action)];
    case SHOW:
      if (index >= 0) {
        state[index].visible = true;
        return state.slice();
      }
      return state;
    case HIDE:
      if (index >= 0) {
        state[index].visible = false;
        return state.slice();
      }
      return state;
    case DESTROY:
      return state.slice(0, -1);
    case SHOW_PROMPT:
      state[index].showPrompt = true;
      return state.slice();
    case HIDE_PROMPT:
      state[index].showPrompt = false;
      return state.slice();
    default:
      return state;
  }
}