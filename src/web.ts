import { WebPlugin } from '@capacitor/core';
import { CallObserverPlugin } from './definitions';

export class CallObserverWeb extends WebPlugin implements CallObserverPlugin {
  constructor() {
    super({
      name: 'CallObserver',
      platforms: ['web']
    });
  }

  // async observe(options: { status: string }): Promise<{status: string}> {
  //   console.log('ECHO', options);
  //   return options;
  // }
}

const CallObserver = new CallObserverWeb();

export { CallObserver };
