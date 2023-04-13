

import 'package:flutter/cupertino.dart';
import 'package:nexring_flutter_platform/domain/sleep_stage.dart';

import '../utils/nex_ring_constants.dart';

class OnBleConnectionListener {
  VoidCallback onBleReady;
  ValueChanged<BleState> onBleState;

  OnBleConnectionListener({required this.onBleReady, required this.onBleState});
}

class OnSleepDataLoadListener {
  Function(LoadDataState state, int progress) onSyncDataFromDevice;
  ValueChanged<List<SleepData>?> onOutputSleepData;

  OnSleepDataLoadListener({required this.onOutputSleepData, required this.onSyncDataFromDevice});
}

class OnPPGReadingsListener {
  VoidCallback onPPGReadingsStarted, onPPGReadingsCanceled;
  Function(PPGReadingsState state, int? spo2, int hr) onPPGReadingsCallback;

  OnPPGReadingsListener({
    required this.onPPGReadingsCallback, required this.onPPGReadingsCanceled, required this.onPPGReadingsStarted
});
}