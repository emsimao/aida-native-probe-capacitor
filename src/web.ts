import { WebPlugin } from '@capacitor/core';

import type { AidaNativeProbePlugin, DeviceInfoResult, RotationSample } from './definitions';

/** Browser fallback: the probe is native-only, so every method rejects with NOT_AVAILABLE_ON_WEB. */
export class AidaNativeProbeWeb extends WebPlugin implements AidaNativeProbePlugin {
  async getInfo(): Promise<DeviceInfoResult> {
    throw this.notAvailable();
  }

  async getRotationSample(): Promise<RotationSample> {
    throw this.notAvailable();
  }

  private notAvailable(): Error & { code: string } {
    const err = new Error('AidaNativeProbe is only available in a native (Android/iOS) build.') as Error & {
      code: string;
    };
    err.code = 'NOT_AVAILABLE_ON_WEB';
    return err;
  }
}
