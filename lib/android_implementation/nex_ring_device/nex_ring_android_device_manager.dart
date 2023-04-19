
import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:rxdart/rxdart.dart';
import '../../domain/battery_info.dart';
import '../../domain/ble_device.dart';
import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_device_platform_interface.dart';



class NexRingAndroidDeviceManager extends NexRingAndroidDevicePlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelName);
  final EventChannel _eventChannel = const EventChannel(NexRingConstants.eventChannelNameDeviceManager);

  final PublishSubject<BatteryInfo> _batteryInfoController = PublishSubject();

  NexRingAndroidDeviceManager() {
    _eventChannel.receiveBroadcastStream().listen((r) {
      final json = jsonDecode(r);
      final action = json['action'];
      final data = json['data'];
      if(action == "batteryInfo") {
        _batteryInfoController.sink.add(BatteryInfo.fromJson(data));
      }
    });
  }

  @override
  Stream<BatteryInfo> get batteryInfoStream => _batteryInfoController.stream;

  @override
  Future<bool> bind() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_bind);

  @override
  Future<bool> factoryReset() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_device_factoryReset);

  @override
  void getBatteryInfo() async {
    _methodChannel
        .invokeMethod(NexRingConstants.android_device_getBatteryInfo);
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
