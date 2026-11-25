import Foundation
import UIKit
import CoreMotion

/// Framework-neutral native core of the AIDA native probe (shared by the Cordova and Capacitor plugins).
public final class AidaNativeProbeCore {
    public static let errNoSensor = "NO_SENSOR"
    public static let errTimeout = "TIMEOUT"

    private let motion = CMMotionManager()

    public init() {}

    public func getInfo() -> [String: Any] {
        var sys = utsname()
        uname(&sys)
        let machine = withUnsafePointer(to: &sys.machine) {
            $0.withMemoryRebound(to: CChar.self, capacity: 1) { String(cString: $0) }
        }
        #if targetEnvironment(simulator)
        let isVirtual = true
        let model = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"] ?? machine
        #else
        let isVirtual = false
        let model = machine
        #endif
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
        return [
            "platform": "ios",
            "osVersion": UIDevice.current.systemVersion,
            "model": model,
            "manufacturer": "Apple",
            "isVirtual": isVirtual,
            "appVersion": version ?? NSNull(),
            "installerPackage": NSNull()
        ]
    }

    /// One raw rotation-rate reading (rad/s). Completion: (result, nil) or (nil, (code, message)).
    public func getRotationSample(timeout: TimeInterval,
                                  completion: @escaping ([String: Any]?, (String, String)?) -> Void) {
        guard motion.isGyroAvailable else {
            completion(nil, (AidaNativeProbeCore.errNoSensor, "This device has no gyroscope sensor."))
            return
        }
        var finished = false
        let lock = NSLock()
        func finish(_ r: [String: Any]?, _ e: (String, String)?) -> Bool {
            lock.lock(); defer { lock.unlock() }
            if finished { return false }
            finished = true
            motion.stopGyroUpdates()
            completion(r, e)
            return true
        }
        motion.gyroUpdateInterval = 0.02
        motion.startGyroUpdates(to: OperationQueue()) { data, error in
            if let d = data {
                let bootOffset = Date().timeIntervalSince1970 - ProcessInfo.processInfo.systemUptime
                _ = finish([
                    "x": d.rotationRate.x,
                    "y": d.rotationRate.y,
                    "z": d.rotationRate.z,
                    "timestamp": Int64((bootOffset + d.timestamp) * 1000),
                    "unit": "rad/s",
                    "sensor": "CMMotionManager.gyro"
                ], nil)
            } else if let err = error {
                _ = finish(nil, (AidaNativeProbeCore.errNoSensor, err.localizedDescription))
            }
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + timeout) {
            _ = finish(nil, (AidaNativeProbeCore.errTimeout, "No gyroscope event within \(Int(timeout * 1000)) ms."))
        }
    }
}
