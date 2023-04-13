
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'nex_ring_android_upgrade_manager.dart';

abstract class NexRingAndroidUpgradePlatform extends PlatformInterface {

  NexRingAndroidUpgradePlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidUpgradePlatform _instance = NexRingAndroidUpgradeManager();

  static NexRingAndroidUpgradePlatform get instance => _instance;

  static set instance(NexRingAndroidUpgradePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }



}