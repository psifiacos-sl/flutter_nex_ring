

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import '../../domain/nex_ring_callbacks.dart';
import '../../domain/ppg_readings_extra.dart';
import 'nex_ring_android_health_manager.dart';

abstract class NexRingAndroidHealthPlatform extends PlatformInterface {

  NexRingAndroidHealthPlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidHealthPlatform _instance = NexRingAndroidHealthManager();

  static NexRingAndroidHealthPlatform get instance => _instance;

  static set instance(NexRingAndroidHealthPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<int> getTotalSteps();

  Future<double> getFingerTemperature();

  Future<bool> isTakingPPGReadings();

  void takePPGReadings({bool hrOnly});

  void cancelPPGReadings();

  void setOnPPGReadingsListener(OnPPGReadingsListener listener);

  void stopOnPPGReadingListener();

  Future<PPGSampleModel> takePPGReadingsByTime({int seconds});

}