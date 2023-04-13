package com.psifiacos.nexring_flutter_platform.bt

import android.bluetooth.BluetoothDevice

data class BleDevice(
    val device: BluetoothDevice,
    val color: Int,
    val size: Int,
    var rssi: Int
)