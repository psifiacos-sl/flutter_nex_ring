package com.psifiacos.nexring_flutter_platform

import androidx.annotation.NonNull
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.util.Log
import com.psifiacos.nexring_flutter_platform.bt.*
import com.psifiacos.nexring_flutter_platform.util.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import lib.linktop.nexring.api.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import android.content.Context
import android.app.Activity

/** NexringFlutterPlatformPlugin */
class NexringFlutterPlatformPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel : MethodChannel
  private lateinit var eventChannelHandler: EventChannelHandler

  private lateinit var context: Context
  private lateinit var activity: Activity

  private val bleManager by lazy {
   // NexRingManager.init(activity.application)
    BleManager(context)
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, Constants.methodChannel)
    channel.setMethodCallHandler(this)
    eventChannelHandler = EventChannelHandler(flutterPluginBinding.binaryMessenger,
      Constants.eventChannel)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  private val onBleConnectionListener = object : OnBleConnectionListener {
    override fun onBleReady() {
      postDelay {
        loge("MainActivityChannel", "onBleReady")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onBleReady")
        }.toString())
      }
    }

    override fun onBleState(state: Int) {
      postDelay {
        loge("MainActivityChannel", "onBleState $state")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onBleState")
          put("data", when (state) {
            BluetoothProfile.STATE_CONNECTING -> 1
            BluetoothProfile.STATE_CONNECTED -> 2
            BluetoothProfile.STATE_DISCONNECTING -> 3
            else -> 0
          })
        }.toString())
      }
    }
  }

  private val onPPGReadings = object : OnPPGReadingsListener {

    override fun onCallbackPPGReadings(state: Int, spo2: Int?, heartRate: Int) {
      postDelay {
        loge("MainActivityChannel", "onCallbackPPGReadings")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onCallbackPPGReadings")
          put("data", JSONObject().apply {
            put("state", when(state) {
              STATE_TAKE_READINGS_NOT_STARTED -> 0
              STATE_TAKING_READINGS -> 1
              else -> 2
            })
            put("spo2", spo2)
            put("heartRate", heartRate)
          })
        }.toString())
      }
    }

    override fun onTakePPGReadingsCanceled() {
      postDelay {
        loge("MainActivityChannel", "onTakePPGReadingsCanceled")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onTakePPGReadingsCanceled")
        }.toString())
      }
    }

    override fun onTakePPGReadingsStarted() {
      postDelay {
        loge("MainActivityChannel", "onTakePPGReadingsStarted")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onTakePPGReadingsStarted")
        }.toString())
      }
    }
  }

  private val onSleepDataLoaded = object : OnSleepDataLoadListener {

    override fun onSyncDataFromDevice(state: Int, progress: Int) {
      postDelay {
        loge("MainActivityChannel", "onSyncDataFromDevice")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onSyncDataFromDevice")
          put("data", JSONObject().apply {
            put("state", when(state) {
              LOAD_DATA_STATE_START -> 0
              LOAD_DATA_STATE_PROCESSING -> 1
              else -> 2
            })
            put("value", progress)
          })
        }.toString())
      }
    }

    override fun onOutputNewSleepData(sleepData: ArrayList<SleepData>?) {
      postDelay {
        loge("MainActivityChannel", "onOutputSleepData ${sleepData?.size}")
        eventChannelHandler.sendEvent(JSONObject().apply {
          put("action", "onOutputSleepData")
          put("data", if(sleepData == null) {
            null
          } else {
            JSONArray().apply {
              sleepData.forEach { sleepModel ->
                val jsonArraySleepStages = JSONArray().apply {
                  sleepModel.sleepStages.forEach {
                    val jsonObject = JSONObject()
                    jsonObject.put("start", it.startT)
                    jsonObject.put("end", it.endT)
                    jsonObject.put("state", when(it.state) {
                      SLEEP_STATE_WAKE -> 0
                      SLEEP_STATE_REM -> 1
                      SLEEP_STATE_LIGHT -> 2
                      else -> 3
                    })
                    put(jsonObject)
                  }
                }
                val jsonArraySleepStates = JSONArray().apply {
                  sleepModel.sleepStates.forEachIndexed { state, it ->
                    val jsonObject = JSONObject()
                    jsonObject.put("state", when(state) {
                      SLEEP_STATE_WAKE -> 0
                      SLEEP_STATE_REM -> 1
                      SLEEP_STATE_LIGHT -> 2
                      else -> 3
                    })
                    jsonObject.put("duration", it.duration)
                    jsonObject.put("percent", it.percent)
                    put(jsonObject)
                  }
                }
                put(JSONObject().apply {
                  put("startTs", sleepModel.startTs)
                  put("endTs", sleepModel.endTs)
                  put("duration", sleepModel.duration)
                  put("efficiency", sleepModel.efficiency)
                  put("hr", sleepModel.hr)
                  put("hrv", sleepModel.hrv)
                  put("br", sleepModel.rr)
                  put("spo2", sleepModel.spo2)
                  put("btMac", sleepModel.btMac)
                  put("hrDip", sleepModel.hrDip)
                  put("sleepStages", jsonArraySleepStages)
                  put("sleepStates", jsonArraySleepStates)
                })
              }
            }
          })
        }.toString())
      }
    }
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    if(call.method.startsWith("bt_")) {
      btMethodCallHandler(call, result)
    } else if(call.method.startsWith("device_")) {
      deviceApiMethodCallHandler(call, result)
    } else if(call.method.startsWith("health_")) {
      healthApiMethodCallHandler(call, result)
    } else if(call.method.startsWith("sleep_")) {
      sleepApiMethodCallHandler(call, result)
    } else if(call.method.startsWith("upgrade_")) {
      upgradeApiMethodCallHandler(call, result)
    } else if(call.method.startsWith("settings_")) {
      settingsApiMethodCallHandler(call, result)
    }
  }

  @SuppressLint("MissingPermission")
  private fun btMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.bt_isBleSupported -> {
        val res = bleManager.isSupportBle()
        result.success(res)
      }
      Constants.bt_getBleState -> {
        val res = bleManager.bleState
        result.success(
          when (res) {
            BluetoothProfile.STATE_CONNECTING -> 1
            BluetoothProfile.STATE_CONNECTED -> 2
            BluetoothProfile.STATE_DISCONNECTING -> 3
            else -> 0
          }
        )
      }
      Constants.bt_isBleScanning -> {
        val res = bleManager.isScanning
        result.success(res)
      }
      Constants.bt_startBleScan -> {
        bleManager.startScan(20 * 1000L,
          object : OnBleScanCallback {
            override fun onScanning(result: BleDevice) {
              val color: Int = when(result.color) {
                PRODUCT_COLOR_DEEP_BLACK -> 0
                PRODUCT_COLOR_GOLDEN -> 1
                else -> 2
              }
              channel.invokeMethod("onBleScanning", JSONObject().apply {
                put("color", color)
                put("rssi", result.rssi)
                put("size", result.size)
                put("device", JSONObject().apply {
                  put("address", result.device.address)
                  put("type", result.device.type)
                  put("name", result.device.name)
                  put("bondState", when(result.device.bondState) {
                    BluetoothDevice.BOND_NONE -> 0
                    BluetoothDevice.BOND_BONDING -> 1
                    else -> 2
                  })
                })
              }.toString())
            }

            override fun onScanFinished() {
              channel.invokeMethod("onBleScanFinished", null)
            }
          })
      }
      Constants.bt_setBleConnectionListener -> {
        bleManager.addOnBleConnectionListener(onBleConnectionListener)
      }
      Constants.bt_stopBleConnectionListener -> {
        bleManager.removeOnBleConnectionListener(onBleConnectionListener)
      }
      Constants.bt_stopBleScan -> {
        bleManager.cancelScan()
      }
      Constants.bt_connectBleDevice -> {
        val res = bleManager.connect(call.argument<String>("device")!!)
        result.success(res)
      }
      Constants.bt_disconnectBleDevice -> {
        bleManager.disconnect()
        result.success(true)
      }
      Constants.bt_getConnectedDevice -> {
        val res = bleManager.connectedDevice
        result.success(res?.let { JSONObject().apply {
          put("address", res.address)
          put("type", res.type)
          put("name", res.name)
          put("bondState", when(res.bondState) {
            BluetoothDevice.BOND_NONE -> 0
            BluetoothDevice.BOND_BONDING -> 1
            else -> 2
          })
        }.toString() })
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun healthApiMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.health_getTotalSteps -> {
        NexRingManager.get().healthApi().getTotalSteps { steps ->
          result.success(steps)
        }
      }
      Constants.health_getFingerTemperature -> {
        NexRingManager.get().healthApi().getFingerTemperature { temp ->
          result.success(temp)
        }
      }
      Constants.health_isTakingPPGReadings -> {
        val res = NexRingManager.get().healthApi().isTakingPPGReadings()
        result.success(res)
      }
      Constants.health_takePPGReadings -> {
        NexRingManager.get().healthApi().takePPGReadings(call.argument<Boolean>("onlyHr") ?: false)
      }
      Constants.health_takePPGReadingsByTime -> {
        val seconds = call.argument<Int>("seconds")!!
        val values = ArrayList<PPGReadExtra>()
        var screenshot = 0.0
        var start by Delegates.notNull<Long>()
        var end by Delegates.notNull<Long>()
        NexRingManager.get().healthApi().setOnPGReadingsListener(object : OnPPGReadingsListener {

          override fun onCallbackPPGReadings(state: Int, spo2: Int?, heartRate: Int) {
            if(state == STATE_TAKING_READINGS_VALID) {
              val rr : Double = 60 / (heartRate * 1.000000000000000)
              screenshot += rr
              if(screenshot >= seconds) {
                NexRingManager.get().healthApi().cancelTakePPGReadings()
              } else {
                Log.e("TakePPGOneMinute", "onTakePPGReadings: HR: $heartRate BPM, OX: $spo2 %, RR: $rr, Instant: $screenshot")
                values.add(PPGReadExtra(heartRate, spo2, rr, screenshot))
              }
            }
          }

          override fun onTakePPGReadingsCanceled() {
            end = System.currentTimeMillis()
            Log.e("TakePPGOneMinute", "onTakePPGReadingsCanceled")
            result.success(JSONObject().apply {
              put("start", start)
              put("end", end)
              put("list", JSONArray().apply {
                values.forEach {
                  put(JSONObject().apply {
                    put("hr", it.hr)
                    put("spo2", it.spo2)
                    put("RR", it.RR)
                    put("beat", it.beatInstant)
                  })
                }
              })
            }.toString())
          }

          override fun onTakePPGReadingsStarted() {
            start = System.currentTimeMillis()
            Log.e("TakePPGOneMinute", "onTakePPGReadingsStarted")
          }
        })
        NexRingManager.get().healthApi().takePPGReadings(false)
      }
      Constants.health_cancelPPGReadings -> {
        NexRingManager.get().healthApi().cancelTakePPGReadings()
      }
      Constants.health_setOnPPGReadingsListener -> {
        NexRingManager.get().healthApi().setOnPGReadingsListener(onPPGReadings)
      }
      Constants.health_stopOnPPGReadingsListener -> {
        NexRingManager.get().healthApi().setOnPGReadingsListener(null)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun sleepApiMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.sleep_getTotalSteps -> {
        NexRingManager.get().sleepApi().getTotalSteps(
          call.argument<String>("btMac")!!, call.argument<Long>("ts")?.let { Calendar.Builder().setInstant(it).build() }
            ?: todayCalendar()) { data ->
          result.success(data?.let { JSONObject().apply {
            put("ts", it.ts)
            put("value", it.value)
          }.toString() })
        }
      }
      Constants.sleep_getRhr -> {
        NexRingManager.get().sleepApi().getRhr(call.argument<String>("btMac")!!, call.argument<Long>("ts")?.let { Calendar.Builder().setInstant(it).build() }
          ?: todayCalendar()) {
          result.success(it)
        }
      }
      Constants.sleep_getFingerTemperatureList -> {
        val startTs = call.argument<Long>("startTs")!!
        val endTs = call.argument<Long>("endTs")!!
        val btMac = call.argument<String>("btMac")!!
        NexRingManager.get().sleepApi().getFingerTemperatureList(btMac, startTs, endTs) {
          result.success(it?.let { pairData ->
            val jsonArrayData = JSONArray().apply {
              pairData.second.forEach { doubleData ->
                val jsonObject = JSONObject()
                jsonObject.put("ts", doubleData.ts)
                jsonObject.put("value", doubleData.value)
                put(jsonObject)
              }
            }
            JSONObject().apply {
              put("avg", pairData.first.avg)
              put("max", pairData.first.max)
              put("min", pairData.first.min)
              put("data", jsonArrayData)
            }.toString()
          })
        }
      }
      Constants.sleep_getHrList -> {
        val startTs = call.argument<Long>("startTs")!!
        val endTs = call.argument<Long>("endTs")!!
        val btMac = call.argument<String>("btMac")!!
        NexRingManager.get().sleepApi().getHrList(btMac, startTs, endTs) {
          result.success(it?.let { pairData ->
            val jsonArrayData = JSONArray().apply {
              pairData.second.forEach { intData ->
                val jsonObject = JSONObject()
                jsonObject.put("ts", intData.ts)
                jsonObject.put("value", intData.value)
                put(jsonObject)
              }
            }
            JSONObject().apply {
              put("avg", pairData.first.avg)
              put("max", pairData.first.max)
              put("min", pairData.first.min)
              put("data", jsonArrayData)
            }.toString()
          })
        }
      }
      Constants.sleep_getHrvList -> {
        val startTs = call.argument<Long>("startTs")!!
        val endTs = call.argument<Long>("endTs")!!
        val btMac = call.argument<String>("btMac")!!
        NexRingManager.get().sleepApi().getHrvList(btMac, startTs, endTs) {
          result.success(it?.let { pairData ->
            val jsonArrayData = JSONArray().apply {
              pairData.second.forEach { intData ->
                val jsonObject = JSONObject()
                jsonObject.put("ts", intData.ts)
                jsonObject.put("value", intData.value)
                put(jsonObject)
              }
            }
            JSONObject().apply {
              put("avg", pairData.first.avg)
              put("max", pairData.first.max)
              put("min", pairData.first.min)
              put("data", jsonArrayData)
            }.toString()
          })
        }
      }
      Constants.sleep_getSleepDataByDate -> {
        NexRingManager.get().sleepApi().getSleepDateByDate(call.argument<String>("btMac")!!, call.argument<Long>("ts")?.let { Calendar.Builder().setInstant(it).build() }
          ?: todayCalendar()) { sleepData ->
          if(sleepData == null) {
            result.success(JSONObject().apply {
              put("data", null)
            }.toString())
          } else {
            val data = JSONArray().apply {
              sleepData.forEach { sleepModel ->
                val jsonArraySleepStages = JSONArray().apply {
                  sleepModel.sleepStages.forEach {
                    val jsonObject = JSONObject()
                    jsonObject.put("start", it.startT)
                    jsonObject.put("end", it.endT)
                    jsonObject.put("state", when(it.state) {
                      SLEEP_STATE_WAKE -> 0
                      SLEEP_STATE_REM -> 1
                      SLEEP_STATE_LIGHT -> 2
                      else -> 3
                    })
                    put(jsonObject)
                  }
                }
                val jsonArraySleepStates = JSONArray().apply {
                  sleepModel.sleepStates.forEachIndexed { state, it ->
                    val jsonObject = JSONObject()
                    jsonObject.put("state", when(state) {
                      SLEEP_STATE_WAKE -> 0
                      SLEEP_STATE_REM -> 1
                      SLEEP_STATE_LIGHT -> 2
                      else -> 3
                    })
                    jsonObject.put("duration", it.duration)
                    jsonObject.put("percent", it.percent)
                    put(jsonObject)
                  }
                }
                put(JSONObject().apply {
                  put("startTs", sleepModel.startTs)
                  put("endTs", sleepModel.endTs)
                  put("duration", sleepModel.duration)
                  put("efficiency", sleepModel.efficiency)
                  put("hr", sleepModel.hr)
                  put("hrv", sleepModel.hrv)
                  put("br", sleepModel.rr)
                  put("spo2", sleepModel.spo2)
                  put("btMac", sleepModel.btMac)
                  put("hrDip", sleepModel.hrDip)
                  put("sleepStages", jsonArraySleepStages)
                  put("sleepStates", jsonArraySleepStates)
                })
              }
            }
            result.success(JSONObject().apply {
              put("data", data)
            }.toString())
          }
        }
      }
      Constants.sleep_setOnSleepDataLoadListener -> {
        NexRingManager.get().sleepApi().setOnSleepDataLoadListener(onSleepDataLoaded)
      }
      Constants.sleep_stopOnSleepDataLoadListener -> {
        NexRingManager.get().sleepApi().setOnSleepDataLoadListener(null)
      }
      Constants.sleep_syncDataFromDev -> {
        NexRingManager.get().sleepApi().syncDataFromDev()
      }
      Constants.sleep_getDayCount -> {
        NexRingManager.get().sleepApi().getDayCount(call.argument<String>("btMac")!!) {
          result.success(it)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun upgradeApiMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.upgrade_reboot -> {}
      Constants.upgrade_upgrade -> {}
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun deviceApiMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.device_bind -> {
        NexRingManager.get().deviceApi().bind {
          result.success(it == 0)
        }
      }
      Constants.device_getDeviceSN -> {
        NexRingManager.get().deviceApi().getSN {
          result.success(it)
        }
      }
      Constants.device_reboot -> {
        NexRingManager.get().deviceApi().reboot()
        result.success(true)
      }
      Constants.device_shutdown -> {
        NexRingManager.get().deviceApi().shutdown()
        result.success(true)
      }
      Constants.device_unbind -> {
        NexRingManager.get().deviceApi().unbind {
          result.success(it == 0)
        }
      }
      Constants.device_factoryReset -> {
        NexRingManager.get().deviceApi().factoryReset()
        result.success(true)
      }
      Constants.device_getBatteryInfo -> {
        NexRingManager.get().deviceApi().getBatteryInfo {
          result.success(JSONObject().apply {
            put("voltage", it.voltage)
            put("level", it.level)
            put("state", if(it.state == BATTERY_STATE_DISCHARGING) 0 else 1)
          }.toString())
        }
      }
      Constants.device_getBindState -> {
        NexRingManager.get().deviceApi().getBindState {
          result.success(it)
        }
      }
      Constants.device_getDeviceInfo -> {
        NexRingManager.get().deviceApi().getDeviceInfo {
          result.success(JSONObject().apply {
            put("productColor", it.productColor)
            put("productSize", it.productSize)
            put("bluetoothAddress", it.bluetoothAddress)
            put("firmwareVersion", it.firmwareVersion)
          }.toString())
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun settingsApiMethodCallHandler(call: MethodCall, result: MethodChannel.Result) {
    when(call.method) {
      Constants.settings_timestampSync -> {
        NexRingManager.get().settingsApi().timestampSync(call.argument<Long>("ts")!!) {
          result.success(it == 0)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

}

data class PPGReadExtra(val hr: Int, val spo2: Int?, val RR: Double, val beatInstant: Double)
