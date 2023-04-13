package com.psifiacos.nexring_flutter_platform.bt

interface OnBleConnectionListener {

    fun onBleState(state: Int)

    fun onBleReady()
}