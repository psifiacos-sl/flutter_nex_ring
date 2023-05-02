
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'nex_ring_android_settings_manager.dart';

abstract class NexRingAndroidSettingsPlatform extends PlatformInterface {

  NexRingAndroidSettingsPlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidSettingsPlatform _instance = NexRingAndroidSettingsManager();

  static NexRingAndroidSettingsPlatform get instance => _instance;

  static set instance(NexRingAndroidSettingsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> timestampSync({int? ts});

}