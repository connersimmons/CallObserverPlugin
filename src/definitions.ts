declare global {
  interface PluginRegistry {
    CallObserver?: CallObserverPlugin;
  }
}

export interface CallObserverPlugin {
  // observe(options: { status: string }): Promise<{status: string}>;
  observe(): Promise<{status: string}>;
}
