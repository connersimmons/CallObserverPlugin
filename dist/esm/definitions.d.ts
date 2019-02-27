declare global {
    interface PluginRegistry {
        CallObserver?: CallObserverPlugin;
    }
}
export interface CallObserverPlugin {
    observe(): Promise<{
        status: string;
    }>;
}
