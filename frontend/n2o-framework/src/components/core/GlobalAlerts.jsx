import React from 'react';
import map from 'lodash/map';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import Alerts from '../snippets/Alerts/Alerts';
import { GLOBAL_KEY } from '../../constants/alerts';
import { makeAlertsByKeySelector } from '../../selectors/alerts';
import { removeAlert } from '../../actions/alerts';

/**
 * Глобальные алерты
 * @param {array} alerts - массив алертов
 * @param {function} onDismiss - функция закрытия
 * @returns {JSX}
 */
export function GlobalAlerts({ alerts, onDismiss }) {
  const handleDismiss = alertId => alertId && onDismiss(alertId);
  const mapAlertsProps = () =>
    map(alerts, alert => ({
      ...alert,
      key: alert.id,
      onDismiss: () => handleDismiss(alert.id),
      className: 'd-inline-flex mb-0 p-2 mw-100',
      details: alert.stacktrace,
      animate: true,
      position: 'relative',
    }));
  return (
    <div className="n2o-global-alerts d-flex justify-content-center">
      <Alerts alerts={mapAlertsProps()} />
    </div>
  );
}

GlobalAlerts.propTypes = {
  alerts: PropTypes.array,
};

GlobalAlerts.defaultProps = {
  alerts: [],
};

const mapStateToProps = createStructuredSelector({
  alerts: (state, props) => makeAlertsByKeySelector(GLOBAL_KEY)(state, props),
});

const mapDispatchToProps = dispatch => ({
  onDismiss: alertId => dispatch(removeAlert(GLOBAL_KEY, alertId)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GlobalAlerts);
