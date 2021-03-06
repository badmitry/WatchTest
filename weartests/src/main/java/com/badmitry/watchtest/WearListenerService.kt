package com.badmitry.watchtest

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService : WearableListenerService() {
//    override fun onDataChanged(dataEvents: DataEventBuffer) {
//        var dataMap: DataMap
//        for (event in dataEvents) {
//
//            // Check the data type
//            if (event.type == DataEvent.TYPE_CHANGED) {
//                // Check the data path
//                val path = event.dataItem.uri.path
//                if (path == WearActivity.WEARABLE_DATA_PATH) {
//                }
//                dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
//                Log.v("!!!", "DataMap received on watch: $dataMap")
//            }
//        }
//    }
//
//
//    override fun onMessageReceived(messageEvent: MessageEvent) {
//        Log.v("!!!", "DataMap received on watch: $messageEvent")
//        if (messageEvent.path == WearActivity.WEARABLE_DATA_PATH) {
//            val message = String(messageEvent.data)
//            val messageIntent = Intent()
//            messageIntent.setAction(Intent.ACTION_SEND)
//            messageIntent.putExtra("message", message)
//            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
//        } else {
//            super.onMessageReceived(messageEvent)
//        }
//    }

    private val START_ACTIVITY = "/start_activity"
    override fun onMessageReceived(messageEvent: MessageEvent) {
        println("!!! $START_ACTIVITY")
        if (messageEvent.path.equals(START_ACTIVITY, ignoreCase = true)) {
            val intent = Intent(this, WearActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }
}