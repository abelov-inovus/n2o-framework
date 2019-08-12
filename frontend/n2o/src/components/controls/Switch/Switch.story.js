import React from 'react';
import { storiesOf, forceReRender } from '@storybook/react';
import { StateDecorator, Store } from '@sambego/storybook-state';
import withForm from 'N2oStorybook/decorators/withForm';
import Switch from './Switch';
import SwitchJson from './Switch.meta.json';
import Factory from '../../../core/factory/Factory';

const store = new Store({
  checked: false,
});

store.subscribe(forceReRender);

const stories = storiesOf('Контролы/Переключатель', module);
const form = withForm({ src: SwitchJson.src });

stories.addDecorator(StateDecorator(store));
stories.addParameters({
  info: {
    propTables: [Switch],
    propTablesExclude: [Factory],
  },
});

stories
  .add('Компонент', () => {
    const props = {
      disabled: false,
      checked: store.get('checked'),
    };

    return (
      <Switch
        {...props}
        checked={store.get('checked')}
        onChange={() => store.set({ checked: !store.state.checked })}
      />
    );
  })
  .add('Метаданные', form(() => ({})));
