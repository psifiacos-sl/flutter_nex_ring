
import 'package:flutter/cupertino.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import '../domain/ble_device.dart';
import '../domain/nex_ring_callbacks.dart';
import '../utils/nex_ring_constants.dart';
import 'nex_ring_android_manager.dart';
import 'nex_ring_device/nex_ring_android_device_platform_interface.dart';
import 'nex_ring_health/nex_ring_android_health_platform_interface.dart';
import 'nex_ring_sleep/nex_ring_android_sleep_platform_interface.dart';
import 'nex_ring_upgrade/nex_ring_android_upgrade_platform_interface.dart';

abstract class NexRingAndroidPlatform extends PlatformInterface {

  NexRingAndroidPlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidPlatform _instance = NexRingAndroidManager();

  static NexRingAndroidPlatform get instance => _instance;

  static set instance(NexRingAndroidPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  NexRingAndroidDevicePlatform get deviceApi => NexRingAndroidDevicePlatform.instance;

  NexRingAndroidHealthPlatform get healthApi => NexRingAndroidHealthPlatform.instance;

  NexRingAndroidUpgradePlatform get upgradeApi => NexRingAndroidUpgradePlatform.instance;

  NexRingAndroidSleepPlatform get sleepApi => NexRingAndroidSleepPlatform.instance;

  Future<bool> isBleSupported();

  Future<BleState> getBleState();

  Future<bool> isBleScanning();

  Future<bool> connectBleDevice(String device);

  Future<bool> disconnectBleDevice();

  void stopBleScan();

  void startBleScan(VoidCallback onBleScanFinished,
      ValueChanged<BleDevice> onBleScanning);

  void setBleConnectionListener(OnBleConnectionListener listener);

  void stopBleConnectionListener();

  Future<BluetoothDevice?> getConnectedDevice();

  void clearBtGatt();

  void unregisterRingService();

  Future<bool> isRingServiceRegistered();
}
