'use strict';

var core = require('@capacitor/core');

const AidaNativeProbe = core.registerPlugin('AidaNativeProbe', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.AidaNativeProbeWeb()),
});

/** Browser fallback: the probe is native-only, so every method rejects with NOT_AVAILABLE_ON_WEB. */
class AidaNativeProbeWeb extends core.WebPlugin {
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

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    AidaNativeProbeWeb: AidaNativeProbeWeb
});

exports.AidaNativeProbe = AidaNativeProbe;
//# sourceMappingURL=plugin.cjs.js.map
