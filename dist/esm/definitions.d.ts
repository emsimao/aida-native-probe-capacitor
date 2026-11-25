/**
 * AIDA native probe — shared JS API (identical shape on the Cordova side:
 * cordova.plugins.AidaNativeProbe). Rejections carry `code` and `message`:
 *   NO_SENSOR | TIMEOUT | INFO_FAILED | NOT_AVAILABLE_ON_WEB
 */
export interface AidaNativeProbePlugin {
    /** Device and app information the web platform does not expose. */
    getInfo(): Promise<DeviceInfoResult>;
    /** One raw gyroscope (rotation-rate) reading, in rad/s. */
    getRotationSample(options?: RotationSampleOptions): Promise<RotationSample>;
}
export interface DeviceInfoResult {
    platform: 'android' | 'ios';
    osVersion: string;
    model: string;
    manufacturer: string;
    isVirtual: boolean;
    appVersion: string | null;
    /** Android installer package name (e.g. com.android.vending); null on iOS or when side-loaded. */
    installerPackage: string | null;
    /** Android only. */
    sdkInt?: number;
}
export interface RotationSampleOptions {
    /** Give up after this many milliseconds (default 2000). */
    timeoutMs?: number;
}
export interface RotationSample {
    x: number;
    y: number;
    z: number;
    /** Epoch milliseconds of the sensor event. */
    timestamp: number;
    unit: 'rad/s';
    sensor: string;
}
export type AidaNativeProbeErrorCode = 'NO_SENSOR' | 'TIMEOUT' | 'INFO_FAILED' | 'NOT_AVAILABLE_ON_WEB';
