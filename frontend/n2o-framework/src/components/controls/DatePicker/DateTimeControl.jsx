import React from 'react';
import { findDOMNode } from 'react-dom';
import { pick } from 'lodash';
import PropTypes from 'prop-types';
import moment from 'moment';
import { Manager, Reference, Popper } from 'react-popper';
import {
  parseDate,
  mapToValue,
  mapToDefaultTime,
  buildDateFormat,
  MODIFIERS,
} from './utils';
import DateInputGroup from './DateInputGroup';
import PopUp from './PopUp';

/**
 * Компонент DateTimeControl
 * @reactProps {string} type
 * @reactProps {string} defaultTime
 * @reactProps {moment|date|string} value
 * @reactProps {moment|date|string} min
 * @reactProps {moment|date|string} max
 * @reactProps {string} dateDivider
 * @reactProps {function} onChange
 * @reactProps {string} dateFormat
 * @reactProps {string} timeFormat
 * @reactProps {string} outputFormat
 * @reactProps {boolean} disabled
 * @reactProps {string} placeholder
 * @reactProps {string} locale
 * @reactProps {string} timeFormat
 * @reactProps {string} openOnFocus
 */
class DateTimeControl extends React.Component {
  constructor(props) {
    super(props);
    let { value, dateFormat, locale, timeFormat, dateDivider } = props;
    this.format = buildDateFormat(dateFormat, timeFormat, dateDivider);

    this.defaultTime = mapToDefaultTime(
      value,
      props.defaultTime,
      DateTimeControl.defaultInputName,
      timeFormat,
      this.format
    );

    const { defaultTime } = this;
    this.state = {
      inputs: mapToValue(
        value,
        defaultTime,
        this.format,
        locale,
        DateTimeControl.defaultInputName
      ),
      isPopUpVisible: false,
      isTimeSet: {},
      focused: false,
    };

    this.select = this.select.bind(this);
    this.onInputChange = this.onInputChange.bind(this);
    this.onChange = this.onChange.bind(this);
    this.setVisibility = this.setVisibility.bind(this);
    this.setPlacement = this.setPlacement.bind(this);
    this.onClickOutside = this.onClickOutside.bind(this);
    this.markTimeAsSet = this.markTimeAsSet.bind(this);
    this.setInputRef = this.setInputRef.bind(this);
    this.onFocus = this.onFocus.bind(this);
  }

  /**
   * Обработка новых пропсов
   */
  componentWillReceiveProps(props) {
    let { value, dateFormat, locale, timeFormat, dateDivider } = props;
    this.format = buildDateFormat(dateFormat, timeFormat, dateDivider);

    this.defaultTime = mapToDefaultTime(
      value,
      props.defaultTime,
      DateTimeControl.defaultInputName,
      timeFormat,
      this.format
    );

    this.setState({
      inputs: mapToValue(
        value,
        this.defaultTime,
        this.format,
        locale,
        DateTimeControl.defaultInputName
      ),
      // isPopUpVisible: false
    });
  }

  markTimeAsSet(inputName) {
    this.setState({
      isTimeSet: { ...this.state.isTimeSet, [inputName]: true },
    });
  }

  /**
   * Приведение к строке
   */
  dateToString(date) {
    const { outputFormat } = this.props;
    if (date instanceof Date) {
      return date.toString();
    } else if (date instanceof moment) {
      return date.format(outputFormat);
    }
    return date;
  }

  /**
   * вызов onChange
   */
  onChange(inputName) {
    const inputs = { ...this.state.inputs };
    if (inputName === DateTimeControl.defaultInputName) {
      this.props.onChange(this.dateToString(inputs[inputName]));
    } else {
      this.props.onChange([
        this.dateToString(inputs[DateTimeControl.beginInputName]),
        this.dateToString(inputs[DateTimeControl.endInputName]),
      ]);
    }
  }

  /**
   * Выбор даты, прокидывается в календарь
   */
  select(day, inputName, close = true) {
    const { inputs } = this.state;
    let { locale } = this.props;
    if (
      inputName === DateTimeControl.defaultInputName ||
      inputName === DateTimeControl.beginInputName ||
      (inputName === DateTimeControl.endInputName &&
        !inputs[DateTimeControl.beginInputName]) ||
      (inputName === DateTimeControl.endInputName &&
        moment(day).isSameOrAfter(inputs[DateTimeControl.beginInputName]))
    ) {
      const inputValue = () => {
        if (
          inputName === DateTimeControl.beginInputName &&
          inputs[DateTimeControl.endInputName] &&
          moment(day).isAfter(inputs[DateTimeControl.endInputName])
        ) {
          return {
            [inputName]: day,
            [DateTimeControl.endInputName]: null,
          };
        }
        return {
          [inputName]: day,
        };
      };
      this.setState(
        {
          inputs: { ...this.state.inputs, ...inputValue() },
          isPopUpVisible:
            inputName === DateTimeControl.beginInputName ||
            inputName === DateTimeControl.endInputName ||
            !close,
        },
        () => this.onChange(inputName)
      );
    }
  }
  /**
   * Выбор даты, прокидывается в инпут
   * @todo объеденить методы select и onInputChange в 1 метод
   */
  onInputChange(date, inputName) {
    const { timeFormat } = this.props;
    const newDate =
      !timeFormat && inputName === DateTimeControl.endInputName
        ? date
            .add(23, 'h')
            .add(59, 'm')
            .add(59, 's')
        : date;

    this.setState(
      {
        inputs: { ...this.state.inputs, [inputName]: newDate },
      },
      () => this.onChange(inputName)
    );
  }
  /**
   * Устанавливает видимость попапа
   */
  setVisibility(visible) {
    this.setState({
      isPopUpVisible: visible,
      focused: visible,
    });
  }
  /**
   * Устанавливает положение попапа
   */
  setPlacement(placement) {
    this.setState({ placement });
  }

  /**
   * Навешивание листенеров для появления / исчезания попапа
   */
  componentWillMount() {
    if (typeof window !== 'undefined') {
      document.addEventListener('mousedown', this.onClickOutside.bind(this));
      document.addEventListener('touchstart', this.onClickOutside.bind(this));
    }
  }
  /**
   * Удаление листенеров для появления / исчезания попапа после анмаунта
   */
  componentWillUnmount() {
    if (typeof window !== 'undefined') {
      document.removeEventListener('mousedown', this.onClickOutside.bind(this));
      document.removeEventListener(
        'touchstart',
        this.onClickOutside.bind(this)
      );
    }
  }
  /**
   * Обработка клика за пределами попапа
   */
  onClickOutside(e) {
    const datePicker = findDOMNode(this.datePicker);
    const dateInput = findDOMNode(this.inputGroup);

    if (!datePicker) return;
    if (
      e.target.className.includes('n2o-pop-up') ||
      (!datePicker.contains(e.target) && !dateInput.contains(e.target))
    ) {
      if (this.state.focused) {
        if (this.props.type === 'date-interval') {
          const start = this.state.inputs[DateTimeControl.beginInputName];
          const end = this.state.inputs[DateTimeControl.endInputName];
          this.onChange([start, end]);
        }
        this.props.onBlur();
      }
      this.setVisibility(false);
    }
  }
  /**
   * Рендер попапа
   */
  renderPopUp(width) {
    const { disabled, placeholder, max, min, locale, timeFormat } = this.props;
    const { inputs, isPopUpVisible, style, placement } = this.state;
    const popUp = (
      <PopUp
        time={this.defaultTime}
        type={this.props.type}
        isTimeSet={this.state.isTimeSet}
        markTimeAsSet={this.markTimeAsSet}
        timeFormat={timeFormat}
        inputGroup={this.inputGroup}
        ref={popUp => (this.popUp = popUp)}
        placement={placement}
        value={inputs}
        select={this.select}
        setPlacement={this.setPlacement}
        setVisibility={this.setVisibility}
        max={parseDate(max, this.format)}
        min={parseDate(min, this.format)}
        locale={locale}
      />
    );
    return isPopUpVisible && popUp;
  }

  onFocus(e) {
    const { onFocus } = this.props;
    this.setState(
      {
        focused: true,
      },
      () => onFocus(e)
    );
  }

  setInputRef(poperRef) {
    return r => {
      this.inputGroup = r;
      poperRef(r);
    };
  }
  /**
   * Базовый рендер
   */
  render() {
    const {
      disabled,
      placeholder,
      className,
      onBlur,
      autoFocus,
      openOnFocus,
    } = this.props;
    const { inputs } = this.state;
    const dateInputGroupProps = pick(this.props, ['max', 'min']);

    return (
      <div className="n2o-date-picker-container">
        <div className="n2o-date-picker" ref={c => (this.datePicker = c)}>
          <Manager>
            <Reference>
              {({ ref }) => (
                <DateInputGroup
                  inputRef={this.setInputRef(ref)}
                  dateFormat={this.format}
                  disabled={disabled}
                  placeholder={placeholder}
                  value={inputs}
                  onInputChange={this.onInputChange}
                  inputClassName={className}
                  setVisibility={this.setVisibility}
                  setWidth={this.setWidth}
                  onBlur={this.onBlur}
                  onFocus={this.onFocus}
                  autoFocus={autoFocus}
                  openOnFocus={openOnFocus}
                  {...dateInputGroupProps}
                />
              )}
            </Reference>
            {this.state.isPopUpVisible && (
              <Popper
                placement="bottom-start"
                modifiers={MODIFIERS}
                positionFixed={true}
              >
                {({ ref, style, placement }) => (
                  <div
                    ref={ref}
                    style={style}
                    data-placement={placement}
                    className="n2o-pop-up"
                  >
                    {this.renderPopUp(this.width)}
                  </div>
                )}
              </Popper>
            )}
          </Manager>
        </div>
      </div>
    );
  }
}

DateTimeControl.defaultInputName = 'singleDate';
DateTimeControl.beginInputName = 'begin';
DateTimeControl.endInputName = 'end';

DateTimeControl.defaultProps = {
  onFocus: () => {},
  onBlur: () => {},
  onChange: () => {},
  dateDivider: ' ',
  dateFormat: 'DD.MM.YYYY',
  outputFormat: 'DD.MM.YYYY HH:mm:ss',
  locale: 'ru',
  autoFocus: false,
  openOnFocus: false,
};

DateTimeControl.propTypes = {
  onFocus: PropTypes.func,
  onBlur: PropTypes.func,
  type: PropTypes.oneOf(['date-interval', 'date-picker']),
  defaultTime: PropTypes.string,
  value: PropTypes.oneOfType([
    PropTypes.instanceOf(moment),
    PropTypes.instanceOf(Date),
    PropTypes.string,
    PropTypes.array,
  ]),
  min: PropTypes.oneOfType([
    PropTypes.instanceOf(moment),
    PropTypes.instanceOf(Date),
    PropTypes.string,
  ]),
  max: PropTypes.oneOfType([
    PropTypes.instanceOf(moment),
    PropTypes.instanceOf(Date),
    PropTypes.string,
  ]),
  dateDivider: PropTypes.string,
  onChange: PropTypes.func,
  dateFormat: PropTypes.string,
  timeFromat: PropTypes.string,
  outputFormat: PropTypes.string,
  disabled: PropTypes.bool,
  placeholder: PropTypes.string,
  locale: PropTypes.oneOf(['en', 'ru']),
  timeFormat: PropTypes.string,
  autoFocus: PropTypes.bool,
  openOnFocus: PropTypes.bool,
};

export default DateTimeControl;