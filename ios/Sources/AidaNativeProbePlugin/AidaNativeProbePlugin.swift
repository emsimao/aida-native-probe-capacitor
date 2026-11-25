import Foundation
import Capacitor

/// Capacitor bridge for the AIDA native probe. JS: window.CapacitorPlugins.AidaNativeProbe
@objc(AidaNativeProbePlugin)
public class AidaNativeProbePlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "AidaNativeProbePlugin"
    public let jsName = "AidaNativeProbe"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getRotationSample", returnType: CAPPluginReturnPromise)
    ]
    private let core = AidaNativeProbeCore()

    @objc func getInfo(_ call: CAPPluginCall) {
        call.resolve(core.getInfo())
    }

    @objc func getRotationSample(_ call: CAPPluginCall) {
        let timeoutMs = call.getDouble("timeoutMs") ?? 2000
        core.getRotationSample(timeout: timeoutMs / 1000) { result, error in
            if let r = result {
                call.resolve(r)
            } else {
                call.reject(error?.1 ?? "Unknown error", error?.0 ?? "UNKNOWN")
            }
        }
    }
}
