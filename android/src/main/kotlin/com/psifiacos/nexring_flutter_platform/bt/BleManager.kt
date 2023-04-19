package com.psifiacos.nexring_flutter_platform.bt

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.psifiacos.nexring_flutter_platform.util.*
import lib.linktop.nexring.*
import lib.linktop.nexring.api.*
import java.util.*
import kotlin.collections.ArrayList

class BleManager(private val context: Context) {

    private val tag = "NexRingSDK:BleManager"
    private val mBluetoothAdapter =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val mOnBleConnectionListeners: MutableList<OnBleConnectionListener> = ArrayList()

    private var mOnBleScanCallback: OnBleScanCallback? = null
    private var bleGatt: BluetoothGatt? = null
    private val scanDevMacList: MutableList<String> = ArrayList()
    var isScanning = false

    private val mScanCallback = object : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val scanRecord = result.scanRecord
            if (scanRecord != null) {
                val bytes = scanRecord.bytes
                if (bytes.matchFromAdvertisementData()) {
                    val address = result.device.address
                    if (!scanDevMacList.contains(address)) {
                        val colorSize = bytes.toColorSize()
                        val bleDevice = BleDevice(
                            device = result.device,
                            color = colorSize.first,
                            size = colorSize.second,
                            rssi = result.rssi
                        )
                        scanDevMacList.add(address)
                        mOnBleScanCallback?.apply {
                            post {
                                onScanning(bleDevice)
                            }
                        }
                    }
                }
            }
        }
    }

    private val scanStopRunnable = Runnable {
        cancelScan()
    }

    var bleState = 0
    var connectedDevice: BluetoothDevice? = null

    private val mGattCallback = object : NexRingBluetoothGattCallback(NexRingManager.get()) {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            gatt: BluetoothGatt, status: Int, newState: Int,
        ) {
            super.onConnectionStateChange(gatt, status, newState)
            loge(
                tag,
                "onConnectionStateChange->status:$status, newState:$newState"
            )
            when (newState) {
                BluetoothProfile.STATE_DISCONNECTED -> {
                    NexRingManager.get().apply {
                        healthApi().apply {
                            if(isTakingPPGReadings()) {
                                cancelTakePPGReadings();
                            }
                        }
                        setBleGatt(null)
                        unregisterRingService()
                    }
                    connectedDevice = null
                    gatt.close()
                    bleState = BluetoothProfile.STATE_DISCONNECTED
                    postBleState()
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    bleState = BluetoothProfile.STATE_CONNECTING
                    postBleState()
                }
                BluetoothProfile.STATE_CONNECTED -> {
                    bleState = BluetoothProfile.STATE_CONNECTED
                    connectedDevice = gatt.device
                    postBleState()
                    gatt.discoverServices()
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            loge(tag, "onServicesDiscovered(), status:${status}")
            // Refresh device cache. This is the safest place to initiate the procedure.
            if (status == BluetoothGatt.GATT_SUCCESS) {
                NexRingManager.get().setBleGatt(gatt)
                logi(tag, "onServicesDiscovered(), registerHealthData")
                postDelay {
                    NexRingManager.get().registerRingService()
                    logi(tag, "onServiceDiscovered(), ringServiceRegistered")
                }
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int,
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val gattStatusSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isRingServiceRegistered = NexRingManager.get().isRingServiceRegistered()
            logi(tag, "onDescriptionWrite(), gattStatusSuccess: $gattStatusSuccess, isRingServiceRegistered: $isRingServiceRegistered")
            if (gattStatusSuccess &&
                isRingServiceRegistered
            ) {
                post {
                    //you need to synchronize the timestamp with the device first after
                    //the the service registration is successful.
                    logi(tag, "onDescriptionWrite(), timestampSync")
                    NexRingManager.get()
                        .settingsApi()
                        .timestampSync(System.currentTimeMillis()) {
                            logi(tag, "onDescriptionWrite(), onBleReady")
                            synchronized(mOnBleConnectionListeners) {
                                mOnBleConnectionListeners.forEach {
                                    it.onBleReady()
                                }
                            }
                        }
                }
            }
        }
    }

    @SuppressLint("MissingPermission", "ObsoleteSdkInt")
    private fun connectInterval(device: BluetoothDevice) {
        loge(tag, "connect gatt to ${device.address}")
        bleGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            device.connectGatt(context, false, gattCallback)
            device.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, mGattCallback)
        }.apply { connect() }
    }

    fun isSupportBle(): Boolean =
//         Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
        context.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)


    @SuppressLint("MissingPermission")
    fun startScan(timeoutMillis: Long, callback: OnBleScanCallback) {
        isScanning = true
        mOnBleScanCallback = callback
        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        mBluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, mScanCallback)
        postDelay(scanStopRunnable, timeoutMillis)
    }

    @SuppressLint("MissingPermission")
    fun cancelScan() {
        if (isScanning) {
            isScanning = false
            mBluetoothAdapter.bluetoothLeScanner.stopScan(mScanCallback)
            post {
                mOnBleScanCallback?.onScanFinished()
                mOnBleScanCallback = null
                scanStopRunnable.handlerRemove()
            }
        }
        scanDevMacList.clear()
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        loge("JKL", "Trying to get remote device, $address")
        val remoteDevice = mBluetoothAdapter.getRemoteDevice(address)
        loge("JKL", "connect to remoteDevice by address, ${remoteDevice.address}")
        return if (!remoteDevice.address.isNullOrEmpty()) {
            connect(remoteDevice)
            true
        } else {
            loge("JKL", "reject, because it cannot connect success.")
            false
        }
    }

    fun connect(device: BluetoothDevice) {
        val delayConnect = isScanning
        cancelScan()
        if (delayConnect) {
            loge("JKL", "connect to ${device.address}, delay 200L")
            postDelay({
                loge("JKL", "delay finish, connect to ${device.address}")
                connectInterval(device)
            }, 200L)
        } else {
            loge("JKL", "connect to ${device.address} right now.")
            connectInterval(device)
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bleGatt?.disconnect()
        bleGatt = null
    }


    fun addOnBleConnectionListener(listener: OnBleConnectionListener) {
        synchronized(mOnBleConnectionListeners) {
            mOnBleConnectionListeners.add(listener)
        }
    }

    fun removeOnBleConnectionListener(listener: OnBleConnectionListener) {
        synchronized(mOnBleConnectionListeners) {
            mOnBleConnectionListeners.remove(listener)
        }
    }

    private fun postBleState() {
        post {
            synchronized(mOnBleConnectionListeners) {
                mOnBleConnectionListeners.forEach {
                    it.onBleState(bleState)
                }
            }
        }
    }
}