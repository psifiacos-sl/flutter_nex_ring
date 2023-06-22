import '../utils/nex_ring_constants.dart';

class BleDevice {
  final int rssi, size;
  final BluetoothDevice device;
  final DeviceColor color;
  final int? generation, batteryLevel;
  final BatteryState? batteryState;

  factory BleDevice.fromJson(Map<String, dynamic> json) {
    final natColor = json['color'];
    return BleDevice(
        rssi: json['rssi'],
        size: json['size'],
        generation: json['generation'],
        batteryLevel: json['batteryLevel'],
        batteryState: json['batteryState'] != null
          ? json['batteryState'] == 0 ? BatteryState.discharging : BatteryState.charging
          : null,
        color: natColor == 0
            ? DeviceColor.deepBlack
            : natColor == 1
                ? DeviceColor.golden
                : DeviceColor.silver,
        device: BluetoothDevice.fromJson(json['device']));
  }

  BleDevice(
      {required this.rssi,
      required this.size,
      required this.color,
      required this.device, this.generation, this.batteryState, this.batteryLevel});
}

class BluetoothDevice {
  final String name, address;
  final int type;
  final DeviceBondState bondState;

  factory BluetoothDevice.fromJson(Map<String, dynamic> json) =>
      BluetoothDevice(
          name: json['name'],
          address: json['address'],
          type: json['type'],
          bondState: DeviceBondState.values[json['bondState']]);

  BluetoothDevice(
      {required this.name,
      required this.address,
      required this.type,
      required this.bondState});
}

class DeviceInfo {
  String btAddress, firmwareVersion;
  int size;
  int? generation;
  DeviceColor color;

  DeviceInfo(
      {required this.color,
      required this.size,
        this.generation,
      required this.btAddress,
      required this.firmwareVersion});

  factory DeviceInfo.fromJson(Map<String, dynamic> json) {
    final natColor = json['productColor'];
    return DeviceInfo(
        color: natColor == 0
            ? DeviceColor.deepBlack
            : natColor == 1
                ? DeviceColor.golden
                : DeviceColor.silver,
        size: json['productSize'],
        generation: json['generation'],
        btAddress: json['bluetoothAddress'],
        firmwareVersion: json['firmwareVersion']);
  }
}
