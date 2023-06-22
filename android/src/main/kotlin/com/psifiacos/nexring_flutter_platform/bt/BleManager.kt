package com.psifiacos.nexring_flutter_platform.bt

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.psifiacos.nexring_flutter_platform.NexringFlutterPlatformPlugin
import com.psifiacos.nexring_flutter_platform.util.*
import lib.linktop.nexring.*
import lib.linktop.nexring.api.*
import java.util.*
import kotlin.collections.ArrayList


private const val OEM_STEP_GET_CERTIFICATION_ENABLED = 0
private const val OEM_STEP_CERTIFY = 1
private const val OEM_STEP_TIMESTAMP_SYNC = 2
private const val OEM_STEP_PROCESS_COMPLETED = 3
class BleManager(private val app: Application) {

    private val tag = "NexRingSDK:BleManager"
    private val mBluetoothAdapter =
        (app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

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
                        val bleDevice = bytes.parseScanRecord().run {
                            BleDevice(
                                result.device,
                                color,
                                size,
                                batteryState,
                                batteryLevel,
                                generation,
                                result.rssi
                            )
                        }
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

    val mReceiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            if(BluetoothAdapter.ACTION_STATE_CHANGED == intent?.action) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF &&
                    connectedDevice != null) {
                    bleState = BluetoothProfile.STATE_DISCONNECTED
                    postBleState()
                    NexRingManager.get().apply {
                        healthApi().apply {
                            setOnPGReadingsListener(null)
                            if(isTakingPPGReadings()) {
                                cancelTakePPGReadings()
                            }
                        }
                        setBleGatt(null)
                        unregisterRingService()
                    }
                    connectedDevice = null
                    bleGatt?.close()
                }
            }
        }
    }

    private val mGattCallback = object : NexRingBluetoothGattCallback(NexRingManager.get()) {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            gatt: BluetoothGatt, status: Int, newState: Int,
        ) {
            super.onConnectionStateChange(gatt, status, newState)
            logi(
                tag,
                "onConnectionStateChange->status:$status, newState:$newState"
            )
            if (NexringFlutterPlatformPlugin.isInitialized()) {
                when (newState) {
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        bleState = BluetoothProfile.STATE_DISCONNECTED
                        postBleState()
                        NexRingManager.get().apply {
                            healthApi().apply {
                                setOnPGReadingsListener(null)
                                if(isTakingPPGReadings()) {
                                    cancelTakePPGReadings()
                                }
                            }
                            setBleGatt(null)
                            unregisterRingService()
                        }
                        connectedDevice = null
                        gatt.close()
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
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            logi(tag, "onServicesDiscovered(), status:${status}")
            // Refresh device cache. This is the safest place to initiate the procedure.
            if (status == BluetoothGatt.GATT_SUCCESS) {
                NexRingManager.get().setBleGatt(gatt)
                logi(tag, "onServicesDiscovered(), setBleGatt")
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
//                post {
//                    //you need to synchronize the timestamp with the device first after
//                    //the service registration is successful.
//                    logi(tag, "onDescriptionWrite(), timestampSync")
//                    NexRingManager.get()
//                        .settingsApi()
//                        .timestampSync(System.currentTimeMillis()) {
//                            logi(tag, "onDescriptionWrite(), onBleReady")
//                            synchronized(mOnBleConnectionListeners) {
//                                    try {
//                                        context.unregisterReceiver(mReceiver)
//                                    } catch (e: IllegalArgumentException) {
//                                        logi(tag, "No bleManager.mReceiver registered")
//                                    }
//                                    context.registerReceiver(mReceiver, android.content.IntentFilter(
//                                        BluetoothAdapter.ACTION_STATE_CHANGED))
//                                mOnBleConnectionListeners.forEach {
//                                    it.onBleReady()
//                                }
//                            }
//                        }
//                }
                OemCertificationProcess().start()
            }
        }
    }

    @SuppressLint("MissingPermission", "ObsoleteSdkInt")
    private fun connectInterval(device: BluetoothDevice) {
        loge(tag, "connect gatt to ${device.address}")
        bleGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            device.connectGatt(context, false, gattCallback)
            device.connectGatt(app, false, mGattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(app, false, mGattCallback)
        }.apply { connect() }
    }

    fun isSupportBle(): Boolean =
//         Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
        app.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)


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
    fun cancelScan(notifiy: Boolean = true) {
        if (isScanning) {
            isScanning = false
            mBluetoothAdapter.bluetoothLeScanner.stopScan(mScanCallback)
            post {
                if(notifiy) {
                    mOnBleScanCallback?.onScanFinished()
                }
                mOnBleScanCallback = null
                scanStopRunnable.handlerRemove()
            }
        }
        scanDevMacList.clear()
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        logi("JKL", "Trying to get remote device, $address")
        val remoteDevice = mBluetoothAdapter.getRemoteDevice(address)
        logi("JKL", "connect to remoteDevice by address, ${remoteDevice.address}")
        return if (!remoteDevice.address.isNullOrEmpty()) {
            connect(remoteDevice)
            true
        } else {
            logi("JKL", "reject, because it cannot connect success.")
            false
        }
    }

    private fun connect(device: BluetoothDevice) {
        val delayConnect = isScanning
        cancelScan()
        if (delayConnect) {
            logi("JKL", "connect to ${device.address}, delay 200L")
            postDelay({
                logi("JKL", "delay finish, connect to ${device.address}")
                connectInterval(device)
            }, 200L)
        } else {
            logi("JKL", "connect to ${device.address} right now.")
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

    inner class OemCertificationProcess : Thread() {

        private val innerTag = "NexRing:OemCertificationProcess"
        private val locked = Object()
        private var step = OEM_STEP_GET_CERTIFICATION_ENABLED

        override fun run() {
            while (step < OEM_STEP_PROCESS_COMPLETED) {
                sleep(200L)
                synchronized(locked) {
                    when (step) {
                        OEM_STEP_GET_CERTIFICATION_ENABLED -> {
                            loge(innerTag, "OEM_STEP_GET_CERTIFICATION_ENABLED")
                            NexRingManager.get().securityApi().getOemCertificationEnabled {
                                step = if (it) OEM_STEP_CERTIFY else OEM_STEP_TIMESTAMP_SYNC
                                synchronized(locked) {
                                    locked.notify()
                                }
                            }
                        }

                        OEM_STEP_CERTIFY -> {
                            loge(innerTag, "OEM_STEP_CERTIFY")
                            NexRingManager.get().securityApi().oemCertify { result ->
                                when (result) {
                                    OEM_CERTIFICATION_FAILED_FOR_CHECK_R2 -> {
                                        logi(innerTag, "OEM_CERTIFICATION_FAILED_FOR_CHECK_R2")
                                        step = OEM_STEP_PROCESS_COMPLETED
                                        synchronized(locked) {
                                            locked.notify()
                                        }
                                    }

                                    OEM_CERTIFICATION_FAILED_FOR_DECRYPT -> {
                                        logi(innerTag, "OEM_CERTIFICATION_FAILED_FOR_DECRYPT")
                                        step = OEM_STEP_PROCESS_COMPLETED
                                        synchronized(locked) {
                                            locked.notify()
                                        }
                                    }

                                    OEM_CERTIFICATION_FAILED_FOR_SN_NULL -> {
                                        logi(innerTag, "OEM_CERTIFICATION_FAILED_FOR_SN_NULL")
                                        step = OEM_STEP_PROCESS_COMPLETED
                                        synchronized(locked) {
                                            locked.notify()
                                        }
                                    }

                                    OEM_CERTIFICATION_START -> {
                                        logi(innerTag, "OEM_CERTIFICATION_START")
                                    }

                                    OEM_CERTIFICATION_SUCCESSFUL -> {
                                        logi(innerTag, "OEM_CERTIFICATION_SUCCESSFUL")
                                        step = OEM_STEP_TIMESTAMP_SYNC
                                        synchronized(locked) {
                                            locked.notify()
                                        }
                                    }
                                }
                            }
                        }

                        OEM_STEP_TIMESTAMP_SYNC -> {
                            loge(innerTag, "OEM_STEP_TIMESTAMP_SYNC")
                            NexRingManager.get()
                                .settingsApi()
                                .timestampSync(System.currentTimeMillis()) {
                                    loge(innerTag, "OEM_STEP_TIMESTAMP_SYNC result $it")
                                    synchronized(mOnBleConnectionListeners) {
                                        post {
                                            try {
                                                app.applicationContext.unregisterReceiver(mReceiver)
                                            } catch (e: IllegalArgumentException) {
                                                logi(tag, "No bleManager.mReceiver registered")
                                            }
                                            app.applicationContext.registerReceiver(mReceiver, android.content.IntentFilter(
                                            BluetoothAdapter.ACTION_STATE_CHANGED))
                                            mOnBleConnectionListeners.forEach { listener ->
                                                listener.onBleReady()
                                            }
                                        }
                                    }
                                    step = OEM_STEP_PROCESS_COMPLETED
                                    synchronized(locked) {
                                        locked.notify()
                                    }
                                }
                        }
                    }
                    locked.wait()
                }
            }
            loge(innerTag, "OEM_STEP_PROCESS_COMPLETED")
        }
    }

}