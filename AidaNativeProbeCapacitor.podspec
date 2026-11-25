require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name = 'AidaNativeProbeCapacitor'
  s.version = package['version']
  s.summary = package['description']
  s.license = package['license']
  s.homepage = 'https://example.invalid/aida-native-probe'
  s.author = 'AIDA'
  s.source = { :git => 'https://example.invalid/aida-native-probe.git', :tag => s.version.to_s }
  s.source_files = 'ios/Sources/**/*.{swift,h,m}'
  s.resource_bundles = {
    'AidaNativeProbeCapacitor_Privacy' => ['ios/Sources/AidaNativeProbePlugin/PrivacyInfo.xcprivacy']
  }
  s.frameworks = 'CoreMotion'
  s.ios.deployment_target = '15.0'
  s.dependency 'Capacitor'
  s.swift_version = '5.9'
end
