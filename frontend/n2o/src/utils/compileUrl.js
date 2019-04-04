import { each, isEmpty } from 'lodash';
import pathToRegexp from 'path-to-regexp';
import queryString from 'query-string';
import linkResolver from './linkResolver';

export function getParams(mapping, state) {
  const params = {};
  each(mapping, (options, key) => {
    const value = linkResolver(state, options);
    params[key] = value || undefined;
  });
  return params;
}

export default function compileUrl(
  url,
  { pathMapping = {}, queryMapping = {} } = {},
  state,
  { extraPathParams = {}, extraQueryParams = {} } = {}
) {
  const pathParams = getParams(pathMapping, state);
  const queryParams = getParams(queryMapping, state);
  let compiledUrl = url;
  if (!isEmpty(pathParams)) {
    compiledUrl = pathToRegexp.compile(url)({
      ...pathParams,
      ...extraPathParams,
    });
  }
  if (!isEmpty(queryParams)) {
    compiledUrl = `${compiledUrl}?${queryString.stringify({
      ...queryParams,
      ...extraQueryParams,
    })}`;
  }
  return compiledUrl;
}
