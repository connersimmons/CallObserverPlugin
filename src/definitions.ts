declare global {
    interface PluginRegistry {
        CallObserver?: CallObserverPlugin;
    }
}
export interface CallObserverPlugin {
  observe(): Promise<{ data: PhoneCall[]}>;
}

export interface PhoneCall {
  status: string
}
