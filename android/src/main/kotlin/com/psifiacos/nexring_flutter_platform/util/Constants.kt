package com.psifiacos.nexring_flutter_platform.util

class Constants {

    companion object {
        const val init = "init"
        const val dispose = "dispose"
        const val isInitialized = "isInitialized"

        const val methodChannelBT = "group.com.psifiacos.aniclient/nex_ring/bt"
        const val methodChannelSleep = "group.com.psifiacos.aniclient/nex_ring/sleep"
        const val methodChannelHealth = "group.com.psifiacos.aniclient/nex_ring/health"
        const val methodChannelSettings = "group.com.psifiacos.aniclient/nex_ring/settings"
        const val methodChannelUpgrade = "group.com.psifiacos.aniclient/nex_ring/upgrade"
        const val methodChannelDevice = "group.com.psifiacos.aniclient/nex_ring/device"
        const val eventChannelNameBTManager = "group.com.psifiacos.aniclient/nex_ring/bt/broadcast"
        const val eventChannelNameSleepManager = "group.com.psifiacos.aniclient/nex_ring/sleep/broadcast"
        const val eventChannelNameDeviceManager = "group.com.psifiacos.aniclient/nex_ring/device/broadcast"
        const val eventChannelNameHealthManager = "group.com.psifiacos.aniclient/nex_ring/health/broadcast"

        //Methods BLE
        const val bt_startBleScan = "bt_startBleScan"
        const val bt_stopBleScan = "bt_stopBleScan"
        const val bt_getBleState = "bt_getBleState"
        const val bt_isBleSupported = "bt_isBleSupported"
        const val bt_isBleScanning = "bt_isBleScanning"
        const val bt_connectBleDevice = "bt_connectBleDevice"
        const val bt_disconnectBleDevice = "bt_disconnectBleDevice"
        const val bt_setBleConnectionListener = "bt_setBleConnectionListener"
        const val bt_stopBleConnectionListener = "bt_stopBleConnectionListener"
        const val bt_getConnectedDevice = "bt_getConnectedDevice"
        const val bt_isRingServiceRegistered = "bt_isRingServiceRegistered"
        const val bt_clearBtGatt = "bt_clearBtGatt"
        const val bt_unregisterRingService = "bt_unregisterRingService"

        //Methods Device api
        const val device_bind = "device_bind"
        const val device_unbind = "device_unbind"
        const val device_factoryReset = "device_factoryReset"
        const val device_getBatteryInfo = "device_getBatteryInfo"
        const val device_getBindState = "device_getBindState"
        const val device_getDeviceInfo = "device_getDeviceInfo"
        const val device_getDeviceSN = "device_getDeviceSN"
        const val device_reboot = "device_reboot"
        const val device_shutdown = "device_shutdown"

        //Methods Health api
        const val health_getTotalSteps = "health_getTotalSteps"
        const val health_getFingerTemperature = "health_getFingerTemperature"
        const val health_isTakingPPGReadings = "health_isTakingPPGReadings"
        const val health_cancelPPGReadings = "health_cancelPPGReadings"
        const val health_takePPGReadings = "health_takePPGReadings"
        const val health_takePPGReadingsByTime = "health_takePPGReadingsByTime"
        const val health_setOnPPGReadingsListener = "health_setOnPPGReadingsListener"
        const val health_stopOnPPGReadingsListener = "health_stopOnPPGReadingsListener"

        //Methods Sleep api
        const val sleep_getTotalSteps = "sleep_getTotalSteps"
        const val sleep_getFingerTemperatureList = "sleep_getFingerTemperatureList"
        const val sleep_getSleepDataByDate = "sleep_getSleepDataByDate"
        const val sleep_getHrList = "sleep_getHrList"
        const val sleep_getHrvList = "sleep_getHrvList"
        const val sleep_getRhr = "sleep_getRhr"
        const val sleep_setOnSleepDataLoadListener = "sleep_setOnSleepDataLoadListener"
        const val sleep_stopOnSleepDataLoadListener = "sleep_stopOnSleepDataLoadListener"
        const val sleep_syncDataFromDev = "sleep_syncDataFromDev"
        const val sleep_checkOnSynced = "sleep_checkOnSynced"
        const val sleep_getDayCount = "sleep_getDayCount"

        //Methods Upgrade api
        const val upgrade_reboot = "upgrade_reboot"
        const val upgrade_upgrade = "upgrade_upgrade"

        //Methods Settings api
        const val settings_timestampSync = "settings_timestampSync"

    }
}