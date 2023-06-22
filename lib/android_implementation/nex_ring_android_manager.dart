

import 'dart:convert';

import 'package:flutter/services.dart';

import '../domain/ble_device.dart';
import '../domain/nex_ring_callbacks.dart';
import '../utils/nex_ring_constants.dart';
import 'nex_ring_android_platform_interface.dart';


class NexRingAndroidManager extends NexRingAndroidPlatform {
  final MethodChannel _methodChannel = const MethodChannel(NexRingConstants.methodChannelBT);
  final EventChannel _eventChannel = const EventChannel(NexRingConstants.eventChannelNameBTManager);

  OnBleConnectionListener? _bleConnectionListener;

  NexRingAndroidManager() {
    // _methodChannel.setMethodCallHandler((call) async {
    //   switch (call.method) {
    //     case "onBleReady":
    //       _bleConnectionListener?.onBleReady();
    //       break;
    //     case "onBleState":
    //       final data = call.arguments;
    //       _bleConnectionListener?.onBleState(data == 0
    //           ? BleState.disconnected
    //           : data == 1
    //           ? BleState.connecting
    //           : data == 2
    //           ? BleState.connected
    //           : data == 3
    //           ? BleState.disconnecting
    //           : BleState.notSupported);
    //       break;
    //   }
    // });
    _eventChannel.receiveBroadcastStream().listen((r) {
      final json = jsonDecode(r);
      final action = json['action'];
      final data = json['data'];
      switch (action) {
        case "onBleReady":
          _bleConnectionListener?.onBleReady();
          break;
        case "onBleState":
          _bleConnectionListener?.onBleState(data == 0
              ? BleState.disconnected
              : data == 1
              ? BleState.connecting
              : data == 2
              ? BleState.connected
              : data == 3
              ? BleState.disconnecting
              : BleState.notSupported);
          break;
      }
    });
  }

  @override
  Future<bool> isBleSupported() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_bt_isBleSupported);

  @override
  Future<BleState> getBleState() async {
    if (await isBleSupported()) {
      final res =
          await _methodChannel.invokeMethod(NexRingConstants.android_bt_getBleState);
      switch (res) {
        case 0:
          return BleState.disconnected;
        case 1:
          return BleState.connecting;
        case 2:
          return BleState.connected;
        case 3:
          return BleState.disconnecting;
        default:
          return BleState.notSupported;
      }
    }
    return BleState.notSupported;
  }

  @override
  Future<bool> isBleScanning() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_bt_isBleScanning);

  @override
  Future<bool> connectBleDevice(String device) async =>
      await _methodChannel
          .invokeMethod(NexRingConstants.android_bt_connectBleDevice, {'device': device});

  @override
  Future<bool> disconnectBleDevice() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_bt_disconnectBleDevice);

  @override
  void stopBleScan() async =>
      await _methodChannel.invokeMethod(NexRingConstants.android_bt_stopBleScan);

  @override
  void startBleScan(VoidCallback onBleScanFinished,
      ValueChanged<BleDevice> onBleScanning) async {
    if (!(await isBleScanning())) {
      _methodChannel.setMethodCallHandler((call) async {
        switch (call.method) {
          case "onBleScanFinished":
            onBleScanFinished();
            break;
          case "onBleScanning":
            final model = BleDevice.fromJson(jsonDecode(call.arguments));
            onBleScanning(model);
            break;
        }
      });
      _methodChannel.invokeMethod(NexRingConstants.android_bt_startBleScan);
    }
  }

  @override
  void setBleConnectionListener(OnBleConnectionListener listener) async {
    if (_bleConnectionListener != null) {
      stopBleConnectionListener();
    }
    _bleConnectionListener = listener;
    _methodChannel.invokeMethod(NexRingConstants.android_bt_setBleConnectionListener);
  }

  @override
  void stopBleConnectionListener() {
    _bleConnectionListener = null;
    _methodChannel.invokeMethod(NexRingConstants.android_bt_stopBleConnectionListener);
  }

  @override
  Future<BluetoothDevice?> getConnectedDevice() async {
    final res = await _methodChannel.invokeMethod(NexRingConstants.android_bt_getConnectedDevice);
    return res != null ? BluetoothDevice.fromJson(jsonDecode(res)) : null;
  }

  @override
  void clearBtGatt() {
    _methodChannel.invokeMethod(NexRingConstants.bt_clearBtGatt);
  }

  @override
  Future<bool> isRingServiceRegistered() async => await _methodChannel.invokeMethod(NexRingConstants.bt_isRingServiceRegistered);

  @override
  void unregisterRingService() {
    _methodChannel.invokeMethod(NexRingConstants.bt_unregisterRingService);
  }

  @override
  Future<bool> disposeSDK() async {
    return await _methodChannel.invokeMethod(NexRingConstants.dispose);
  }

  @override
  Future<bool> initSDK() async {
    return await _methodChannel.invokeMethod(NexRingConstants.init);
  }

  @override
  Future<bool> get isInitialized async => await _methodChannel.invokeMethod(NexRingConstants.isInitialized);
}
