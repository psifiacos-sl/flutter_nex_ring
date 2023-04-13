
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import '../../domain/battery_info.dart';
import '../../domain/ble_device.dart';
import 'nex_ring_android_device_manager.dart';



abstract class NexRingAndroidDevicePlatform extends PlatformInterface {

  NexRingAndroidDevicePlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidDevicePlatform _instance = NexRingAndroidDeviceManager();

  static NexRingAndroidDevicePlatform get instance => _instance;

  static set instance(NexRingAndroidDevicePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String> getDeviceSN();

  Future<bool> bind();

  Future<bool> unbind();

  Future<DeviceInfo> getDeviceInfo();

  Future<bool> factoryReset();

  Future<bool> reboot();

  Future<BatteryInfo> getBatteryInfo();

  Future<bool> getBindState();

  Future<bool> shutdown();

}