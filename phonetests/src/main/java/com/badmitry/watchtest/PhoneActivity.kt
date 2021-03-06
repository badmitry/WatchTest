package com.badmitry.watchtest

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.badmitry.watchtest.databinding.PhoneMainActivityBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.util.*


class PhoneActivity : Activity(),
    ConnectionCallbacks, MessageApi.MessageListener {
    private val START_ACTIVITY = "/start_activity"
    private val WEAR_MESSAGE_PATH = "/message"
    private val PHONE_MESSAGE_PATH = "/pmessage"
    private var mApiClient: GoogleApiClient? = null

    private var binding: PhoneMainActivityBinding? = null
    var googleClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        println("!!!" + BuildConfig.APPLICATION_ID)
        binding = DataBindingUtil.setContentView(this, R.layout.phone_main_activity)
        googleClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()
        googleClient?.connect()
        binding?.btn?.setOnClickListener {
            val currentTime = Calendar.getInstance().time
            sendAsyncMessage(
                WEAR_MESSAGE_PATH,
                currentTime.toString()
            )
//            val dataMap = DataMap().apply {
//                this.putString(VALUE, currentTime.toString())
//            }
//            googleClient?.let {
//                PhoneSendToDataLayerThread(it, currentTime.toString()).start()
//            }
        }
        initGoogleApiClient()
    }

    private fun sendAsyncMessage(wearMessagePath: String, message: String) {
        Wearable.NodeApi.getConnectedNodes(mApiClient).setResultCallback { nodes ->
            for (node in nodes.nodes) {
                Wearable.MessageApi.sendMessage(
                    mApiClient, node.id, wearMessagePath, message.toByteArray()
                ).setResultCallback { sendMessageResult ->
                    if (sendMessageResult.status.isSuccess) {
                        showToastMT(message)
                    }
                }
            }
        }
    }

    private fun showToastMT(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, "message send $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initGoogleApiClient() {
        mApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()
        mApiClient?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mApiClient != null) mApiClient!!.unregisterConnectionCallbacks(this)
        mApiClient!!.disconnect()
    }

    fun setText(text: String) {
        binding?.text?.text = text
    }

    override fun onConnected(p0: Bundle?) {
        sendAsyncMessage(START_ACTIVITY, "")
        Wearable.MessageApi.addListener(mApiClient, this)
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

    override fun onResume() {
        super.onResume()
        if (mApiClient != null && !(mApiClient!!.isConnected || mApiClient!!.isConnecting)) mApiClient!!.connect()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        println("!!! $START_ACTIVITY")
        runOnUiThread {
            if (messageEvent?.getPath().equals(
                    PHONE_MESSAGE_PATH,
                    ignoreCase = true
                )
            ) {
                setText(messageEvent?.getData().toString())
            }
        }
    }
}