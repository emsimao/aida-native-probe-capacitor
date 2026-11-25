package com.aida.nativeprobe;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONObject;

/** Capacitor bridge for the AIDA native probe. JS: window.CapacitorPlugins.AidaNativeProbe */
@CapacitorPlugin(name = "AidaNativeProbe")
public class AidaNativeProbePlugin extends Plugin {

    @PluginMethod
    public void getInfo(PluginCall call) {
        try {
            call.resolve(JSObject.fromJSONObject(AidaNativeProbeCore.getInfo(getContext())));
        } catch (Exception e) {
            call.reject(e.getMessage(), AidaNativeProbeCore.ERR_INFO_FAILED);
        }
    }

    @PluginMethod
    public void getRotationSample(final PluginCall call) {
        long timeout = call.getLong("timeoutMs", 2000L);
        AidaNativeProbeCore.getRotationSample(getContext(), timeout, new AidaNativeProbeCore.Callback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    call.resolve(JSObject.fromJSONObject(result));
                } catch (Exception e) {
                    call.reject(e.getMessage(), AidaNativeProbeCore.ERR_INFO_FAILED);
                }
            }

            @Override
            public void onError(String code, String message) {
                call.reject(message, code);
            }
        });
    }
}
