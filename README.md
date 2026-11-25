# aida-native-probe-capacitor

Probe plugin for native mobile app tests: device info and one raw gyroscope sample. Capacitor side of a dual-stack plugin;
the Cordova side is [aida-native-probe](https://github.com/emsimao/aida-native-probe). Plugin name `AidaNativeProbe`.

```js
AidaNativeProbe.getInfo()                       // {platform, osVersion, model, manufacturer, isVirtual, appVersion, installerPackage}
AidaNativeProbe.getRotationSample({timeoutMs})  // {x, y, z, timestamp, unit: "rad/s", sensor}
```
Both return Promises; errors are `{code, message}` with code `NO_SENSOR`, `TIMEOUT`, `INFO_FAILED`, `NOT_AVAILABLE_ON_WEB` (web).
`dist/` is committed so the package installs from a git tag without a build step.
