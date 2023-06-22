
class NexRingConstants {

  static const methodChannelBT = "group.com.psifiacos.aniclient/nex_ring/bt";
  static const methodChannelSleep = "group.com.psifiacos.aniclient/nex_ring/sleep";
  static const methodChannelHealth = "group.com.psifiacos.aniclient/nex_ring/health";
  static const methodChannelSettings = "group.com.psifiacos.aniclient/nex_ring/settings";
  static const methodChannelUpgrade = "group.com.psifiacos.aniclient/nex_ring/upgrade";
  static const methodChannelDevice = "group.com.psifiacos.aniclient/nex_ring/device";


  static const eventChannelNameBTManager = 'group.com.psifiacos.aniclient/nex_ring/bt/broadcast';
  static const eventChannelNameSleepManager = 'group.com.psifiacos.aniclient/nex_ring/sleep/broadcast';
  static const eventChannelNameDeviceManager = 'group.com.psifiacos.aniclient/nex_ring/device/broadcast';
  static const eventChannelNameHealthManager = 'group.com.psifiacos.aniclient/nex_ring/health/broadcast';

  static const init = "init";
  static const dispose = "dispose";
  static const isInitialized = "isInitialized";

  ///Methods BLE
  static const android_bt_getBleState = 'bt_getBleState';
  static const android_bt_isBleSupported = 'bt_isBleSupported';
  static const android_bt_startBleScan = 'bt_startBleScan';
  static const android_bt_stopBleScan = 'bt_stopBleScan';
  static const android_bt_isBleScanning = 'bt_isBleScanning';
  static const android_bt_connectBleDevice = 'bt_connectBleDevice';
  static const android_bt_disconnectBleDevice = 'bt_disconnectBleDevice';
  static const android_bt_setBleConnectionListener = 'bt_setBleConnectionListener';
  static const android_bt_stopBleConnectionListener = 'bt_stopBleConnectionListener';
  static const android_bt_getConnectedDevice = 'bt_getConnectedDevice';
  static const bt_isRingServiceRegistered = "bt_isRingServiceRegistered";
  static const bt_clearBtGatt = "bt_clearBtGatt";
  static const bt_unregisterRingService = "bt_unregisterRingService";

  ///Methods Device api
  static const android_device_bind = "device_bind";
  static const android_device_unbind = "device_unbind";
  static const android_device_factoryReset = "device_factoryReset";
  static const android_device_getBatteryInfo = "device_getBatteryInfo";
  static const android_device_getBindState = "device_getBindState";
  static const android_device_getDeviceInfo = "device_getDeviceInfo";
  static const android_device_getDeviceSN = "device_getDeviceSN";
  static const android_device_reboot = "device_reboot";
  static const android_device_shutdown = "device_shutdown";

  ///Methods Health api
  static const android_health_getTotalSteps = "health_getTotalSteps";
  static const android_health_getFingerTemperature = "health_getFingerTemperature";
  static const android_health_isTakingPPGReadings = "health_isTakingPPGReadings";
  static const android_health_cancelPPGReadings = "health_cancelPPGReadings";
  static const android_health_takePPGReadings = "health_takePPGReadings";
  static const android_health_setOnPPGReadingsListener = "health_setOnPPGReadingsListener";
  static const android_health_stopOnPPGReadingsListener = "health_stopOnPPGReadingsListener";
  static const android_health_takePPGReadingsByTime = "health_takePPGReadingsByTime";

  ///Methods Sleep api
  static const android_sleep_getTotalSteps = "sleep_getTotalSteps";
  static const android_sleep_getDayCount = "sleep_getDayCount";
  static const android_sleep_getFingerTemperatureList = "sleep_getFingerTemperatureList";
  static const android_sleep_getSleepDataByDate = "sleep_getSleepDataByDate";
  static const android_sleep_getHrList = "sleep_getHrList";
  static const android_sleep_getHrvList = "sleep_getHrvList";
  static const android_sleep_getRhr = "sleep_getRhr";
  static const android_sleep_setOnSleepDataLoadListener = "sleep_setOnSleepDataLoadListener";
  static const android_sleep_stopOnSleepDataLoadListener = "sleep_stopOnSleepDataLoadListener";
  static const android_sleep_syncDataFromDev = "sleep_syncDataFromDev";
  static const android_sleep_checkOnSynced = "sleep_checkOnSynced";

  ///Methods Upgrade api
  static const android_upgrade_reboot = "upgrade_reboot";
  static const android_upgrade_upgrade = "upgrade_upgrade";

  ///Methods Settings api
  static const android_settings_timestampSync = "settings_timestampSync";
}

enum BleState { notSupported, disconnected, connecting, connected, disconnecting }

enum DeviceColor { deepBlack, golden, silver }

enum DeviceBondState { none, bonding, bonded }

enum BatteryState { discharging, charging }

enum LoadDataState { start, processing, completed }

enum SleepStageEnum { wake, rem, light, deep, nap }

enum PPGReadingsState { notStarted, reading, readingsValid }