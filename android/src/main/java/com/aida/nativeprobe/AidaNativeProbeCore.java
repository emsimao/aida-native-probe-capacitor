package com.aida.nativeprobe;

import android.content.Context;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Framework-neutral native core of the AIDA native probe.
 * Shared verbatim by the Cordova and the Capacitor plugin (same file in both source trees).
 * Results are plain JSONObjects; errors carry one of the shared error codes.
 */
public final class AidaNativeProbeCore {

    public static final String ERR_NO_SENSOR = "NO_SENSOR";
    public static final String ERR_TIMEOUT = "TIMEOUT";
    public static final String ERR_INFO_FAILED = "INFO_FAILED";

    public interface Callback {
        void onSuccess(JSONObject result);
        void onError(String code, String message);
    }

    private AidaNativeProbeCore() {}

    public static JSONObject getInfo(Context ctx) throws JSONException {
        JSONObject r = new JSONObject();
        r.put("platform", "android");
        r.put("osVersion", Build.VERSION.RELEASE);
        r.put("sdkInt", Build.VERSION.SDK_INT);
        r.put("model", Build.MODEL);
        r.put("manufacturer", Build.MANUFACTURER);
        r.put("isVirtual", isEmulator());
        String pkg = ctx.getPackageName();
        String appVersion = null;
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(pkg, 0);
            appVersion = pi.versionName;
        } catch (PackageManager.NameNotFoundException ignored) { }
        r.put("appVersion", appVersion == null ? JSONObject.NULL : appVersion);
        String installer = installerOf(ctx, pkg);
        r.put("installerPackage", installer == null ? JSONObject.NULL : installer);
        return r;
    }

    @SuppressWarnings("deprecation")
    private static String installerOf(Context ctx, String pkg) {
        try {
            PackageManager pm = ctx.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                InstallSourceInfo info = pm.getInstallSourceInfo(pkg);
                return info.getInstallingPackageName();
            }
            return pm.getInstallerPackageName(pkg);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isEmulator() {
        String fp = Build.FINGERPRINT == null ? "" : Build.FINGERPRINT;
        String hw = Build.HARDWARE == null ? "" : Build.HARDWARE;
        String product = Build.PRODUCT == null ? "" : Build.PRODUCT;
        String model = Build.MODEL == null ? "" : Build.MODEL;
        return fp.startsWith("generic") || fp.contains("emulator") || fp.contains("/sdk_")
                || hw.contains("goldfish") || hw.contains("ranchu")
                || product.contains("sdk") || product.contains("emulator")
                || model.contains("Emulator") || model.contains("Android SDK built for");
    }

    /** One raw gyroscope reading (rad/s). Fails with NO_SENSOR or TIMEOUT (timeoutMs). */
    public static void getRotationSample(Context ctx, long timeoutMs, final Callback cb) {
        final SensorManager sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        final Sensor gyro = sm == null ? null : sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyro == null) {
            cb.onError(ERR_NO_SENSOR, "This device has no gyroscope sensor.");
            return;
        }
        final HandlerThread thread = new HandlerThread("AidaNativeProbeGyro");
        thread.start();
        final Handler handler = new Handler(thread.getLooper());
        final AtomicBoolean done = new AtomicBoolean(false);
        final SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent e) {
                if (!done.compareAndSet(false, true)) return;
                sm.unregisterListener(this);
                thread.quitSafely();
                try {
                    // event.timestamp is ns since boot; convert to epoch ms for JS.
                    long epochMs = System.currentTimeMillis()
                            - (SystemClock.elapsedRealtimeNanos() - e.timestamp) / 1_000_000L;
                    JSONObject r = new JSONObject();
                    r.put("x", (double) e.values[0]);
                    r.put("y", (double) e.values[1]);
                    r.put("z", (double) e.values[2]);
                    r.put("timestamp", epochMs);
                    r.put("unit", "rad/s");
                    r.put("sensor", gyro.getName());
                    cb.onSuccess(r);
                } catch (JSONException ex) {
                    cb.onError(ERR_INFO_FAILED, ex.getMessage());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor s, int accuracy) { }
        };
        sm.registerListener(listener, gyro, SensorManager.SENSOR_DELAY_GAME, handler);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!done.compareAndSet(false, true)) return;
                sm.unregisterListener(listener);
                thread.quitSafely();
                cb.onError(ERR_TIMEOUT, "No gyroscope event within " + timeoutMs + " ms.");
            }
        }, timeoutMs);
    }
}
