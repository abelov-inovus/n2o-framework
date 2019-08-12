import React from 'react';
import { storiesOf, forceReRender } from '@storybook/react';
import { StateDecorator, Store } from '@sambego/storybook-state';
import withTests from 'N2oStorybook/withTests';
import withForm from 'N2oStorybook/decorators/withForm';
import Input from './InputText';
import InputJson from './InputText.meta.json';
import Factory from '../../../core/factory/Factory';

const store = new Store({
  value: '',
});

store.subscribe(forceReRender);

const form = withForm({ src: 'InputText' });

const stories = storiesOf('Контролы/Ввод текста', module);

stories.addDecorator(withTests('Input'));

stories.addDecorator(StateDecorator(store));
stories.addParameters({
  info: {
    propTables: [Input],
    propTablesExclude: [Factory],
  },
});

stories
  .add('Компонент', () => (
    <Input
      value={store.get('value')}
      onChange={value => store.set({ value })}
    />
  ))
  .add(
    'Метаданные',
    form(() => {
      const props = {
        placeholder: InputJson.placeholder,
        disabled: InputJson.disabled,
        length: InputJson.length,
      };

      return props;
    })
  );
