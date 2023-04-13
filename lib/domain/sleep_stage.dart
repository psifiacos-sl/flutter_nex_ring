

import '../utils/nex_ring_constants.dart';

class SleepData {
  final String btMacAddress;
  final int startTs, endTs, duration;
  final double efficiency, hrv, hr, hrDip;
  final int? spo2, rr;
  final List<SleepStage> sleepStages;
  final List<SleepState> sleepStates;

  SleepData(
      {required this.startTs,
      required this.endTs,
      required this.duration,
      required this.efficiency,
      required this.hr,
      required this.hrv,
      required this.btMacAddress,
      required this.hrDip,
      required this.sleepStages,
      required this.sleepStates,
      this.rr,
      this.spo2});

  factory SleepData.fromJson(Map<String, dynamic> json) {
    return SleepData(
        startTs: json['startTs'],
        endTs: json['endTs'],
        duration: json['duration'],
        efficiency: json['efficiency'] * 1.0,
        hr: json['hr'] * 1.0,
        hrv: json['hrv'] * 1.0,
        rr: json['rr'] == null ? null : json['rr'] * 1.0,
        btMacAddress: json['btMac'],
        hrDip: json['hrDip'] * 1.0,
        spo2: json['spo2'] == null ? null : ((json['spo2'] * 1.0) as double).round(),
        sleepStages: (json['sleepStages'] as List<dynamic>).map((e) => SleepStage.fromJson(e)).toList(),
        sleepStates: (json['sleepStates'] as List<dynamic>).map((e) => SleepState.fromJson(e)).toList()
    );
  }

}

class SleepStage {
  final double start, end;
  final SleepStageEnum stage;

  SleepStage({required this.stage, required this.end, required this.start});
  
  factory SleepStage.fromJson(Map<String, dynamic> json) {
    final int state = json['state'];
    return SleepStage(stage: SleepStageEnum.values[state], end: json['end'] * 1.0, start: json['start'] * 1.0);
  }
}

class SleepState {
  final int duration;
  final double percent;
  final SleepStageEnum stage;

  SleepState({required this.duration, required this.percent, required this.stage});

  factory SleepState.fromJson(Map<String, dynamic> json) {
    final int state = json['state'];
    return SleepState(duration: json['duration'], percent: json['percent'] * 1.0, stage: SleepStageEnum.values[state]);
  }
}
