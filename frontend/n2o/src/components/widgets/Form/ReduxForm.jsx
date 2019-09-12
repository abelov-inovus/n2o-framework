import React from 'react';
import { compose, withProps } from 'recompose';
import { Prompt } from 'react-router-dom';
import { reduxForm } from 'redux-form';
import Form from './Form';
import ReduxField from './ReduxField';
import createValidator from '../../../core/validation/createValidator';
import PropTypes from 'prop-types';

/**
 *
 * @reactProps {object} props.prompt - флаг включения обработки выхода с несохраненной формы
 * @reactProps {object} props.dirty - флаг отличия формы от initialState
 * @returns {*}
 * @constructor
 */
function ReduxForm(props, context) {
  return (
    <React.Fragment>
      {props.prompt && (
        <Prompt when={props.dirty} message={context.defaultPromptMessage} />
      )}
      <Form {...props} />
    </React.Fragment>
  );
}

ReduxForm.contextTypes = {
  defaultPromptMessage: PropTypes.string,
};

ReduxForm.propTypes = {
  prompt: PropTypes.bool,
};

ReduxForm.defaultProps = {
  prompt: false,
};

ReduxForm.Field = ReduxField;

export default compose(
  withProps(props => {
    const state = props.store && props.store.getState();

    return {
      ...createValidator(props.validation, props.form, state, props.fields),
      ...props,
    };
  }),
  reduxForm({
    destroyOnUnmount: true,
    enableReinitialize: true,
  })
)(ReduxForm);
