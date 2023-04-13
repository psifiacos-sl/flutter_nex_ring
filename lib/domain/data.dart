class IntData {
  final int ts, value;

  IntData({required this.ts, required this.value});

  factory IntData.fromJson(Map<String, dynamic> json) =>
      IntData(value: json['value'], ts: json['ts']);
}

class DoubleData {
  final int ts;
  final double value;

  DoubleData({required this.value, required this.ts});

  factory DoubleData.fromJson(Map<String, dynamic> json) =>
      DoubleData(value: json['value'] * 1.0, ts: json['ts']);
}

class StatisticsData {
  final double max, min, avg;

  StatisticsData({required this.min, required this.avg, required this.max});

  factory StatisticsData.fromJson(Map<String, dynamic> json) =>
      StatisticsData(min: json['min']  * 1.0, avg: json['avg'] * 1.0, max: json['max'] * 1.0);
}

class Pair<T, S> {
  T first;
  S second;

  Pair({required this.first, required this.second});
}
