class PPGReadingsExtra {
  final double rr, beatInstant;
  final int hr;
  final int? spo2;

  PPGReadingsExtra(
      {this.spo2,
      required this.hr,
      required this.beatInstant,
      required this.rr});

  factory PPGReadingsExtra.fromJson(Map<String, dynamic> json) =>
      PPGReadingsExtra(
          hr: json['hr'], spo2: json['spo2'], beatInstant: json['beat'] * 1.0, rr: json['RR'] * 1.0);
}

class PPGSampleModel {
  DateTime start, end;
  List<PPGReadingsExtra> samples;

  PPGSampleModel({required this.start, required this.end, required this.samples});
}
