import { call, put, take, cancelled } from 'redux-saga/effects';
import {
  fetchStart,
  fetchEnd,
  fetchCancel,
  fetchError,
} from '../actions/fetch';
import apiProvider from '../core/api.js';
import { FETCH_ERROR_CONTINUE } from '../constants/fetch';

export default function* fetchSaga(fetchType, options) {
  try {
    yield put(fetchStart(fetchType, options));
    let response = yield call(apiProvider, fetchType, options);
    yield put(fetchEnd(fetchType, options, response));
    return response;
  } catch (error) {
    yield put(fetchError(fetchType, options, error));
    yield take(FETCH_ERROR_CONTINUE);
    throw error;
  } finally {
    if (yield cancelled()) {
      yield put(fetchCancel(fetchType, options));
      return; /* eslint no-unsafe-finally:0 */
    }
  }
}