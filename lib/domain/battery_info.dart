
import '../utils/nex_ring_constants.dart';

class BatteryInfo {
  int voltage, level;
  BatteryState state;

  BatteryInfo(
      {required this.level, required this.state, required this.voltage});

  factory BatteryInfo.fromJson(Map<String, dynamic> json) {
    final natState = json['state'];
    return BatteryInfo(
        level: json['level'],
        state: natState == 0 ? BatteryState.discharging : BatteryState.charging,
        voltage: json['voltage']);
  }
}
