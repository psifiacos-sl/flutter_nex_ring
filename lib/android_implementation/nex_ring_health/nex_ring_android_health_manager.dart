import 'dart:convert';

import 'package:flutter/services.dart';
import '../../domain/nex_ring_callbacks.dart';
import '../../domain/ppg_readings_extra.dart';
import '../../utils/nex_ring_constants.dart';
import 'nex_ring_android_health_platform_interface.dart';

class PPGReadingsCallbackModel {
  final int? spo2;
  final int hr;
  final PPGReadingsState state;

  PPGReadingsCallbackModel({required this.state, required this.hr, this.spo2});
}

class NexRingAndroidHealthManager extends NexRingAndroidHealthPlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(NexRingConstants.methodChannelName);
  final EventChannel _eventChannel = const EventChannel(NexRingConstants.eventChannelName);

  OnPPGReadingsListener? _onPPGReadingsListener;

  NexRingAndroidHealthManager() {
    _eventChannel
        .receiveBroadcastStream()
        .listen((r) {
      final json = jsonDecode(r);
      final action = json['action'];
      final data = json['data'];
      switch (action) {
        case "onTakePPGReadingsCanceled":
          _onPPGReadingsListener?.onPPGReadingsCanceled();
          break;
        case "onTakePPGReadingsStarted":
          _onPPGReadingsListener?.onPPGReadingsStarted();
          break;
        case "onCallbackPPGReadings":
          _onPPGReadingsListener?.onPPGReadingsCallback(
              PPGReadingsState.values[data['state']],
              data['spo2'],
              data['heartRate']);
          break;
      }
    });
  }

  @override
  Future<double> getFingerTemperature() async => await _methodChannel
      .invokeMethod(NexRingConstants.android_health_getFingerTemperature);

  @override
  Future<int> getTotalSteps() async => await _methodChannel
      .invokeMethod(NexRingConstants.android_health_getTotalSteps);

  @override
  void cancelPPGReadings() {
    _methodChannel
        .invokeMethod(NexRingConstants.android_health_cancelPPGReadings);
  }

  @override
  Future<bool> isTakingPPGReadings() async => await _methodChannel
      .invokeMethod(NexRingConstants.android_health_isTakingPPGReadings);

  @override
  void takePPGReadings({bool hrOnly = false}) {
    _methodChannel
        .invokeMethod(NexRingConstants.android_health_takePPGReadings);
  }

  @override
  void setOnPPGReadingsListener(OnPPGReadingsListener listener) {
    if (_onPPGReadingsListener != null) {
      stopOnPPGReadingListener();
    }
    _onPPGReadingsListener = listener;
    _methodChannel
        .invokeMethod(NexRingConstants.android_health_setOnPPGReadingsListener);
  }

  @override
  void stopOnPPGReadingListener() {
    _onPPGReadingsListener = null;
    _methodChannel.invokeMethod(
        NexRingConstants.android_health_stopOnPPGReadingsListener);
  }

  @override
  Future<PPGSampleModel> takePPGReadingsByTime({int seconds = 60}) async {
    final res = await _methodChannel.invokeMethod(NexRingConstants.android_health_takePPGReadingsByTime, {"seconds": seconds});
    final json = jsonDecode(res);
    final list = (json['list'] as List<dynamic>).map((e) => PPGReadingsExtra.fromJson(e)).toList();
    return PPGSampleModel(
        start: DateTime.fromMillisecondsSinceEpoch(json['start']),
        end: DateTime.fromMillisecondsSinceEpoch(json['end']),
        samples: list);
  }
}
