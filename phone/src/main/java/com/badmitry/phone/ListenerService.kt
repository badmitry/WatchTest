package com.badmitry.phone

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import java.util.concurrent.TimeUnit


class ListenerService : WearableListenerService() {
    var googleClient: GoogleApiClient? = null
    companion object {
        val ACTION_SM = "com.rusdelphi.batterywatcher.action.SM"
        val ACTION_SM_PARAM = "com.rusdelphi.batterywatcher.action.SM.PARAM"
    }
    private val WEAR_MESSAGE_PATH = "batterywatcher_message_path"

    override fun onCreate() {
        super.onCreate()
        googleClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .build()
        googleClient?.connect()
    }

    override fun onDestroy() {
        if (null != googleClient && googleClient!!.isConnected) {
            googleClient!!.disconnect()
        }
        super.onDestroy()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action: String? = intent.getAction()
            if (ACTION_SM == action) {
                val param1: String? = intent.getStringExtra(ACTION_SM_PARAM)
                if (googleClient!!.isConnected) {
                    Thread {
                        val nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await()
                        for (node in nodes.nodes) {
                            val result = Wearable.MessageApi.sendMessage(
                                googleClient,
                                node.id,
                                WEAR_MESSAGE_PATH,
                                param1?.toByteArray()
                            ).await()
                            if (result.status.isSuccess) {
                                Log.d(
                                    "main",
                                    "Message phone: {" + param1 + "} sent to: " + node.displayName
                                )
                            } else {
                                // Log an error
                                Log.d("main", "ERROR: failed to send Message")
                            }
                        }
                    }.start()
                }
                if (!googleClient!!.isConnected) Thread {
                    val connectionResult = googleClient!!.blockingConnect(30, TimeUnit.SECONDS)
                    val nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await()
                    for (node in nodes.nodes) {
                        val result = Wearable.MessageApi.sendMessage(
                            googleClient,
                            node.id,
                            WEAR_MESSAGE_PATH,
                            param1?.toByteArray()
                        ).await()
                        if (result.status.isSuccess) {
                            Log.d("main", "Message: {" + param1 + "} sent to: " + node.displayName)
                        } else {
                            // Log an error
                            Log.d("main", "ERROR: failed to send Message")
                        }
                    }
                }.start()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == WEAR_MESSAGE_PATH) {
            val message = String(messageEvent.data)
            val messageIntent = Intent()
            messageIntent.setAction(Intent.ACTION_SEND)
            messageIntent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }
}