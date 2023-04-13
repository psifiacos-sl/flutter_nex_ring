package com.psifiacos.nexring_flutter_platform.bt

import com.psifiacos.nexring_flutter_platform.bt.BleDevice

interface OnBleScanCallback {

    fun onScanning(result: BleDevice)

    fun onScanFinished()
}