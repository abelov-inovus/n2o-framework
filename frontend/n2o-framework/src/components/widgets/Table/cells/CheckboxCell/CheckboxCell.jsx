import React from 'react';
import PropTypes from 'prop-types';
import { get, set, isEqual } from 'lodash';
import { compose, withState, lifecycle, withHandlers } from 'recompose';
import withCell from '../../withCell';
import CheckboxN2O from '../../../../controls/Checkbox/CheckboxN2O';

function CheckboxCell({
  callActionImpl,
  updateFieldInModel,
  model,
  fieldKey,
  id,
  visible,
  disabled,
  callInvoke,
  checked,
  onChange,
  ...rest
}) {
  return (
    visible && (
      <CheckboxN2O
        className="сheckbox-сell"
        inline={true}
        onChange={onChange}
        disabled={disabled}
        checked={checked}
        {...rest}
      />
    )
  );
}

CheckboxCell.propTypes = {
  id: PropTypes.string,
  model: PropTypes.object,
  fieldKey: PropTypes.string,
  className: PropTypes.string,
  callInvoke: PropTypes.func,
  visible: PropTypes.bool,
};

CheckboxCell.defaultProps = {
  visible: true,
  disabled: false,
};

export default compose(
  withCell,
  withState(
    'checked',
    'setChecked',
    ({ model, fieldKey, id }) => model && get(model, fieldKey || id)
  ),
  withHandlers({
    onChange: ({
      callActionImpl,
      callInvoke,
      action,
      setChecked,
      model,
      fieldKey,
      id,
    }) => e => {
      const checked = e.nativeEvent.target.checked;

      const data = set(
        {
          ...model,
        },
        fieldKey || id,
        checked
      );

      setChecked(checked);
      callInvoke(data, get(action, 'options.payload.dataProvider'));
    },
  }),
  lifecycle({
    componentDidUpdate(prevProps) {
      const { model, fieldKey, id } = this.props;

      if (
        !isEqual(
          get(prevProps.model, fieldKey || id),
          get(model, fieldKey || id)
        )
      ) {
        this.setState({ checked: get(model, fieldKey || id) });
      }
    },
  })
)(CheckboxCell);