import { registerPlugin } from '@capacitor/core';

import type { AidaNativeProbePlugin } from './definitions';

const AidaNativeProbe = registerPlugin<AidaNativeProbePlugin>('AidaNativeProbe', {
  web: () => import('./web').then((m) => new m.AidaNativeProbeWeb()),
});

export * from './definitions';
export { AidaNativeProbe };
