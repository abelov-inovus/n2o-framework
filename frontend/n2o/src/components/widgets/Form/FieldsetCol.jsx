import React from 'react';
import { pure } from 'recompose';
import { Col } from 'reactstrap';
import ReduxField from './ReduxField';
import FieldsetContainer from './Fieldset';

function FieldsetCol({
  col,
  defaultCol,
  colId,
  autoFocusId,
  labelPosition,
  labelWidth,
  labelAlignment,
  modelPrefix,
  form,
}) {
  return (
    <Col xs={col.size || defaultCol} key={colId} className={col.className}>
      {col.fields &&
        col.fields.map((field, i) => {
          const autoFocus = field.id && autoFocusId && field.id === autoFocusId;
          const key = 'field' + i;
          return (
            <ReduxField
              labelPosition={labelPosition}
              labelWidth={labelWidth}
              labelAlignment={labelAlignment}
              key={key}
              autoFocus={autoFocus}
              form={form}
              modelPrefix={modelPrefix}
              {...field}
            />
          );
        })}
      {col.fieldsets &&
        col.fieldsets.map((fieldset, i) => {
          const key = `set${i}`;
          return (
            <FieldsetContainer
              modelPrefix={modelPrefix}
              key={key}
              form={form}
              {...fieldset}
            />
          );
        })}
    </Col>
  );
}

export default pure(FieldsetCol);