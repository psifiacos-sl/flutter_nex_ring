import 'dart:convert';


import 'package:flutter/services.dart';

import '../../domain/data.dart';
import '../../domain/nex_ring_callbacks.dart';
import '../../domain/sleep_stage.dart';
import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_sleep_platform_interface.dart';

class NexRingAndroidSleepManager extends NexRingAndroidSleepPlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelName);
  final EventChannel _eventChannel = const EventChannel(NexRingConstants.eventChannelName);

  OnSleepDataLoadListener? _onSleepDataLoadListener;

  NexRingAndroidSleepManager() {
    _eventChannel.receiveBroadcastStream().listen((r) {
      final json = jsonDecode(r);
      final action = json['action'];
      final data = json['data'];
      switch(action) {
        case "onSyncDataFromDevice":
          final state = data['state'];
          _onSleepDataLoadListener?.onSyncDataFromDevice(
              state == 0
                  ? LoadDataState.start
                  : state == 1
                  ? LoadDataState.processing
                  : LoadDataState.completed,
              data['value']);
          break;
        case "onOutputSleepData":
          final List<SleepData>? sleepData = data == null
              ? null
              : (data as List<dynamic>)
              .map((e) => SleepData.fromJson(e))
              .toList();
          _onSleepDataLoadListener?.onOutputSleepData(sleepData);
          break;
      }
    });
  }

  @override
  Future<int> getDayCount(String btMac) async =>
      await _methodChannel.invokeMethod(
          NexRingConstants.android_sleep_getDayCount, {"btMac": btMac});

  @override
  Future<Pair<StatisticsData, List<DoubleData>>?> getFingerTemperatureList(
      int startTs, int endTs, String btMac) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getFingerTemperatureList,
        {"startTs": startTs, "endTs": endTs, "btMac": btMac});
    if (res == null) return res;
    final decoded = jsonDecode(res);
    return Pair(
        first: StatisticsData.fromJson(decoded),
        second: (decoded['data'] as List<dynamic>)
            .map((e) => DoubleData.fromJson(e))
            .toList());
  }

  @override
  Future<List<SleepData>?> getSleepDataByDate(String btMac, {int? ts}) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getSleepDataByDate,
        {"btMac": btMac, "ts": ts});
    final decoded = jsonDecode(res);
    if (decoded['data'] == null) return null;
    final data = List.from(decoded['data'] as List<dynamic>);
    return data.map((e) => SleepData.fromJson(e)).toList();
  }

  @override
  Future<Pair<StatisticsData, List<IntData>>?> getHrList(
      int startTs, int endTs, String btMac) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getHrList,
        {"startTs": startTs, "endTs": endTs, "btMac": btMac});
    if (res == null) return res;
    final decoded = jsonDecode(res);
    return Pair(
        first: StatisticsData.fromJson(decoded),
        second: (decoded['data'] as List<dynamic>)
            .map((e) => IntData.fromJson(e))
            .toList());
  }

  @override
  Future<Pair<StatisticsData, List<IntData>>?> getHrvList(
      int startTs, int endTs, String btMac) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getHrvList,
        {"startTs": startTs, "endTs": endTs, "btMac": btMac});
    if (res == null) return res;
    final decoded = jsonDecode(res);
    return Pair(
        first: StatisticsData.fromJson(decoded),
        second: (decoded['data'] as List<dynamic>)
            .map((e) => IntData.fromJson(e))
            .toList());
  }

  @override
  Future<int?> getRhr(String btMac, {int? ts}) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getRhr, {"btMac": btMac, "ts": ts});
    return res;
  }

  @override
  Future<IntData> getTotalSteps(String btMac, {int? ts}) async {
    final res = await _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_getTotalSteps,
        {"btMac": btMac, "ts": ts});
    return IntData.fromJson(jsonDecode(res));
  }

  @override
  void setOnSleepDataLoadListener(OnSleepDataLoadListener listener) async {
    if (_onSleepDataLoadListener != null) {
      stopOnSleepDataLoadListener();
    }
    _onSleepDataLoadListener = listener;
    _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_setOnSleepDataLoadListener);
  }

  @override
  void stopOnSleepDataLoadListener() {
    _onSleepDataLoadListener = null;
    _methodChannel.invokeMethod(
        NexRingConstants.android_sleep_stopOnSleepDataLoadListener);
  }

  @override
  void syncDataFromDev() async => await _methodChannel
      .invokeMethod(NexRingConstants.android_sleep_syncDataFromDev);
}
