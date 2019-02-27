import { WebPlugin } from '@capacitor/core';
import { CallObserverPlugin } from './definitions';
export declare class CallObserverWeb extends WebPlugin implements CallObserverPlugin {
    constructor();
    observe(options: {
        status: string;
    }): Promise<{
        status: string;
    }>;
}
declare const CallObserver: CallObserverWeb;
export { CallObserver };
