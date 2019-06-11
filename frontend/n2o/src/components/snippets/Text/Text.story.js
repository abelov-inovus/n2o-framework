import React from 'react';
import { storiesOf } from '@storybook/react';
import { withKnobs, text, select } from '@storybook/addon-knobs/react';
import Factory from '../../../core/factory/Factory';
import { SNIPPETS } from '../../../core/factory/factoryLevels';

const stories = storiesOf('UI Компоненты/Текст', module);

stories.addDecorator(withKnobs);
stories.add('Создание через Factory', () => {
  const dt = {
    id: 'uniqId',
    src: 'Text',
    text: text('text', 'Text'),
    format: select(
      'format',
      ['date', 'password', 'number', 'dateFromNow'],
      null
    ),
  };
  return (
    <React.Fragment>
      <Factory level={SNIPPETS} id={'uniqId'} {...dt} />
    </React.Fragment>
  );
});
