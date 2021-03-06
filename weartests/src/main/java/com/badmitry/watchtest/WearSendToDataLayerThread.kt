package com.badmitry.watchtest

import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable

class WearSendToDataLayerThread(private val googleClient: GoogleApiClient,
                                private val date: String): Thread() {

//    override fun run() {
        // Construct a DataRequest and send over the data layer
//        val putDMR = PutDataMapRequest.create(MainActivity.WEARABLE_DATA_PATH)
//        dataMap?.let{
//            putDMR.dataMap.putAll(it)
//            val request = putDMR.asPutDataRequest()
//            val result = Wearable.DataApi.putDataItem(googleClient, request).await()
//            if (result.status.isSuccess) {
//                Log.v("!!!", "DataMap: " + dataMap.toString() + " sent successfully to data layer ")
//            } else {
//                // Log an error
//                Log.v("!!!", "ERROR: failed to send DataMap to data layer")
//            }
//        }
//        val nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await()
//        for (node in nodes.nodes) {
//            val result = Wearable.MessageApi.sendMessage(
//                googleClient,
//                node.id,
//                WearActivity.WEARABLE_DATA_PATH,
//                date?.toByteArray()
//            ).await()
//            if (result.status.isSuccess) {
//                Log.d(
//                    "main",
//                    "Message phone: {" + date + "} sent to: " + node.displayName
//                )
//            } else {
//                // Log an error
//                Log.d("main", "ERROR: failed to send Message")
//            }
//
//        }
//    }
}