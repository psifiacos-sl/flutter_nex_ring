
import 'dart:convert';

import 'package:flutter/services.dart';
import '../../domain/battery_info.dart';
import '../../domain/ble_device.dart';
import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_device_platform_interface.dart';



class NexRingAndroidDeviceManager extends NexRingAndroidDevicePlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelName);

  @override
  Future<bool> bind() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_bind);

  @override
  Future<bool> factoryReset() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_factoryReset);

  @override
  Future<BatteryInfo> getBatteryInfo() async {
    final res = await _methodChannel
        .invokeMethod(NexRingConstants.android_device_getBatteryInfo);
    return BatteryInfo.fromJson(jsonDecode(res));
  }

  @override
  Future<bool> getBindState() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_getBindState);

  @override
  Future<DeviceInfo> getDeviceInfo() async {
    final res = await _methodChannel
        .invokeMethod(NexRingConstants.android_device_getDeviceInfo);
    return DeviceInfo.fromJson(jsonDecode(res));
  }

  @override
  Future<String> getDeviceSN() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_getDeviceSN);

  @override
  Future<bool> reboot() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_reboot);

  @override
  Future<bool> shutdown() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_shutdown);

  @override
  Future<bool> unbind() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_unbind);
}
