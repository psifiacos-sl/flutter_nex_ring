

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import '../../domain/data.dart';
import '../../domain/nex_ring_callbacks.dart';
import '../../domain/sleep_stage.dart';
import 'nex_ring_android_sleep_manager.dart';

abstract class NexRingAndroidSleepPlatform extends PlatformInterface {

  NexRingAndroidSleepPlatform() : super(token: _token);

  static final Object _token = Object();

  static NexRingAndroidSleepPlatform _instance = NexRingAndroidSleepManager();

  static NexRingAndroidSleepPlatform get instance => _instance;

  static set instance(NexRingAndroidSleepPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<IntData?> getTotalSteps(String btMac, {int? ts});

  Future<int> getDayCount(String btMac);

  Future<int?> getRhr(String btMac, {int? ts});

  Future<Pair<StatisticsData, List<DoubleData>>?> getFingerTemperatureList(int startTs, int endTs, String btMac);

  Future<Pair<StatisticsData, List<IntData>>?> getHrList(int startTs, int endTs, String btMac);

  Future<Pair<StatisticsData, List<IntData>>?> getHrvList(int startTs, int endTs, String btMac);

  Future<List<SleepData>?> getSleepDataByDate(String btMac, {int? ts});

  void setOnSleepDataLoadListener(OnSleepDataLoadListener listener);

  void stopOnSleepDataLoadListener();

  void syncDataFromDev();

  Future<bool> checkOnSynced();

}