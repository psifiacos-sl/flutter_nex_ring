
import 'package:flutter/services.dart';

import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_upgrade_platform_interface.dart';



class NexRingAndroidUpgradeManager extends NexRingAndroidUpgradePlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelName);



}
