import { WebPlugin } from '@capacitor/core';
/** Browser fallback: the probe is native-only, so every method rejects with NOT_AVAILABLE_ON_WEB. */
export class AidaNativeProbeWeb extends WebPlugin {
    async getInfo() {
        throw this.notAvailable();
    }
    async getRotationSample() {
        throw this.notAvailable();
    }
    notAvailable() {
        const err = new Error('AidaNativeProbe is only available in a native (Android/iOS) build.');
        err.code = 'NOT_AVAILABLE_ON_WEB';
        return err;
    }
}
//# sourceMappingURL=web.js.map