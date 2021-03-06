package com.badmitry.watchtest

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.badmitry.watchtest.databinding.WearActivityMainBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.util.*

class WearActivity : Activity(),
    MessageListener, ConnectionCallbacks {
    private val WEAR_MESSAGE_PATH = "/message"
    private val PHONE_MESSAGE_PATH = "/pmessage"
    private val START_PHONE_ACTIVITY = "/start_phone_activity"

    private var binding: WearActivityMainBinding? = null
    var mApiClient: GoogleApiClient? = null
//    var wearSendToDataLayerThread: WearSendToDataLayerThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        println("!!!" + BuildConfig.APPLICATION_ID)
        binding = DataBindingUtil.setContentView(this, R.layout.wear_activity_main)
        binding?.btn?.setOnClickListener {
            val currentTime = Calendar.getInstance().time
//            val dataMap = DataMap().apply {
//                this.putString(VALUE, currentTime.toString())
//            }
            mApiClient?.let {
                sendMessage(PHONE_MESSAGE_PATH, currentTime.toString())
//                WearSendToDataLayerThread(it, currentTime.toString()).start()
            }
        }
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    initGoogleApiClient()
    }

    private fun initGoogleApiClient() {
        mApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()

        if (mApiClient != null && !(mApiClient!!.isConnected || mApiClient!!.isConnecting)) mApiClient!!.connect()
    }

    private fun sendMessage(phoneMessagePath: String, message: String) {
        Thread {
            val nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await()
            for (node in nodes.nodes) {
                val result = Wearable.MessageApi.sendMessage(
                    mApiClient, node.id, phoneMessagePath, message.toByteArray()
                ).await()
            }
            runOnUiThread {
                Toast.makeText(applicationContext, "m to phon$message", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        if (mApiClient != null && !(mApiClient!!.isConnected || mApiClient!!.isConnecting)) mApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mApiClient != null) {
            Wearable.MessageApi.removeListener(mApiClient, this)
            if (mApiClient!!.isConnected) {
                mApiClient!!.disconnect()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (mApiClient != null) mApiClient!!.unregisterConnectionCallbacks(this)
        super.onDestroy()
    }

    private val START_ACTIVITY = "/start_activity"

    fun setText(text: String) {
        binding?.text?.text = text
    }

    override fun onConnected(p0: Bundle?) {
        sendMessage(START_PHONE_ACTIVITY, "")
        Wearable.MessageApi.addListener(mApiClient, this)
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        println("!!! $START_ACTIVITY")
        runOnUiThread {
            if (messageEvent?.path.equals(WEAR_MESSAGE_PATH, ignoreCase = true)) {
                setText(messageEvent?.getData().toString())
            }
        }
    }
}