
import 'package:flutter/services.dart';

import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_settings_platform_interface.dart';



class NexRingAndroidSettingsManager extends NexRingAndroidSettingsPlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelSettings);

  @override
  Future<bool> timestampSync({int? ts}) async => await _methodChannel.invokeMethod(NexRingConstants.android_settings_timestampSync, {"ts": ts});

}
