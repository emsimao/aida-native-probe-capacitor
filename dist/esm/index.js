import { registerPlugin } from '@capacitor/core';
const AidaNativeProbe = registerPlugin('AidaNativeProbe', {
    web: () => import('./web').then((m) => new m.AidaNativeProbeWeb()),
});
export * from './definitions';
export { AidaNativeProbe };
//# sourceMappingURL=index.js.map