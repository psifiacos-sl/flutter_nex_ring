package com.psifiacos.nexring_flutter_platform.util

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class EventChannelHandler(binaryMessenger: BinaryMessenger, channel: String) {
    private var eventSink: EventChannel.EventSink? = null
    private var eventChannel: EventChannel = EventChannel(binaryMessenger, channel).apply {
        setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                eventSink = events
            }

            override fun onCancel(arguments: Any?) {
                eventSink = null
            }
        })
    }
    fun sendEvent(message: String?) {
        if (eventSink != null) {
            eventSink!!.success(message)
        }
    }
}