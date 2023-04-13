package com.psifiacos.nexring_flutter_platform.util

import android.util.Log
import lib.linktop.nexring.BuildConfig
import java.util.*

fun todayCalendar(): Calendar =
    Calendar.getInstance().apply {
        this[Calendar.HOUR_OF_DAY] = 0
        this[Calendar.MINUTE] = 0
        this[Calendar.SECOND] = 0
        this[Calendar.MILLISECOND] = 0
    }

fun loge(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun loge(tag: String, msg: String, e: Throwable) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg, e)
    }
}

fun loge(msg: String) = loge("NexRingSDK", msg)

fun loge(msg: String, e: Throwable) = loge("NexRingSDK", msg, e)

fun logi(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg)
    }
}