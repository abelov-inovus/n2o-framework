import linkResolver from './linkResolver';
import moment from 'moment';

const config = {
  link: '',
  value: ''
};

const state = {
  a: {
    b: {
      c: 'test'
    }
  }
};

describe('Проверка linkResolver', () => {
  it('пустой конфиг', () => {
    const res = linkResolver(state, {});
    expect(res).toBe(undefined);
  });
  it('только link', () => {
    const res = linkResolver(state, {
      link: 'a.b.c'
    });
    expect(res).toBe('test');
  });
  it('только link - кривой путь', () => {
    const res = linkResolver(state, {
      link: 'q.w.e'
    });
    expect(res).toBe(undefined);
  });
  it('только value - константа', () => {
    const res = linkResolver(state, {
      value: 1
    });
    expect(res).toBe(1);
  });
  it('только value - js expression', () => {
    const res = linkResolver(state, {
      value: '`2+2`'
    });
    expect(res).toBe(4);
  });
  it('value и link', () => {
    const res = linkResolver(state, {
      link: 'a.b.c',
      value: '`this+"-n2o"`'
    });
    expect(res).toBe('test-n2o');
  });
  it('value (константа) и link', () => {
    const res = linkResolver(state, {
      link: 'a.b.c',
      value: '123'
    });
    expect(res).toBe('123');
  });
  it('value и link (кривой)', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: '`this.x`'
    });
    expect(res).toBe(undefined);
  });
  it('value (this) и link (кривой)', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: '`this`'
    });
    expect(res).toEqual({});
  });

  // Типизация linkResolver

  it('value с типом number', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: 5
    });
    expect(res).toEqual(5);
  });

  it('value с типом string', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: '5'
    });
    expect(res).toEqual('5');
  });

  it('value с типом string c js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: '`1 + 3`'
    });
    expect(res).toEqual(4);
  });

  it('value с типом object', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: { key: '5' }
    });
    expect(res).toEqual({ key: '5' });
  });

  it('value с типом object c js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: { key: '`1 + 4`' }
    });
    expect(res).toEqual({ key: 5 });
  });

  it('value с типом обьект (вложение 3)', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: {
        key1: {
          key2: {
            key3: '5'
          }
        }
      }
    });
    expect(res).toEqual({
      key1: {
        key2: {
          key3: '5'
        }
      }
    });
  });

  it('value с типом обьект (вложение 3) c js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: {
        key1: {
          key2: {
            key3: '`1 + 4`'
          }
        }
      }
    });
    expect(res).toEqual({
      key1: {
        key2: {
          key3: 5
        }
      }
    });
  });

  it('value с типом массив', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: ['4', '5', '6']
    });
    expect(res).toEqual(['4', '5', '6']);
  });

  it('value с типом массив с js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: ['`1+3`', '`1+4`', '`1+5`']
    });
    expect(res).toEqual([4, 5, 6]);
  });

  it('value с типом коллекция', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: [{ key: '4' }, { key: '5' }]
    });
    expect(res).toEqual([{ key: '4' }, { key: '5' }]);
  });

  it('value с типом коллекция с js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: [{ key: '`1+3`' }, { key: '`1+4`' }]
    });
    expect(res).toEqual([{ key: 4 }, { key: 5 }]);
  });

  it('value с типом коллекция (вложенные обьекты)', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: [{ key: { key1: '4' } }, { key: { key1: '5' } }]
    });
    expect(res).toEqual([{ key: { key1: '4' } }, { key: { key1: '5' } }]);
  });

  it('value с типом коллекция (вложенные обьекты) с js выражением', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: [{ key: { key1: '`1+3`' } }, { key: { key1: '`1+4`' } }]
    });
    expect(res).toEqual([{ key: { key1: 4 } }, { key: { key1: 5 } }]);
  });

  it('value с типом коллекция (вложенные обьекты) с js data', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: {
        end: "`$.now({ dateFormat: 'DD.MM.YYYY', timeFormat: 'HH:mm:ss' })`",
        begin: "`$.now({ dateFormat: 'DD.MM.YYYY', timeFormat: 'HH:mm:ss' })`"
      }
    });
    expect(res).toEqual({
      begin: moment().format('DD.MM.YYYY HH:mm:ss'),
      end: moment().format('DD.MM.YYYY HH:mm:ss')
    });
  });
  it('value и link (кривой), value обьект', () => {
    const res = linkResolver(state, {
      link: 'q.w.e',
      value: { key: '`this.x`' }
    });
    expect(res).toEqual({});
  });
});
