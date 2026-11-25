import { WebPlugin } from '@capacitor/core';
import type { AidaNativeProbePlugin, DeviceInfoResult, RotationSample } from './definitions';
/** Browser fallback: the probe is native-only, so every method rejects with NOT_AVAILABLE_ON_WEB. */
export declare class AidaNativeProbeWeb extends WebPlugin implements AidaNativeProbePlugin {
    getInfo(): Promise<DeviceInfoResult>;
    getRotationSample(): Promise<RotationSample>;
    private notAvailable;
}
