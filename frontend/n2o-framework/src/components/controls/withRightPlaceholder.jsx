import React from 'react';
import PropTypes from 'prop-types';

const withRightPlaceholder = WrappedComponent => {
  return function(props) {
    const { measure } = props;

    return (
      <div className="n2o-control-container">
        <WrappedComponent {...props} />
        {!!measure ? (
          <div className="n2o-control-container-placeholder">{measure}</div>
        ) : null}
      </div>
    );
  };
};

withRightPlaceholder.propTypes = {
  measure: PropTypes.string,
};

withRightPlaceholder.defaultProps = {
  measure: '',
};

export default withRightPlaceholder;
