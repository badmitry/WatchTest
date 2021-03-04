package com.badmitry.watch

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.badmitry.watchtest.R
import java.text.DecimalFormat

class MainActivity : Activity() {
    private var mWatch: TextView? = null
    private var mSmartphone: TextView? = null
    var messageReceiver: MessageReceiver = MessageReceiver()
    private var mBatteryLevelReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWatch = findViewById(R.id.charge_watch) as TextView?
        mSmartphone = findViewById(R.id.charge_phone) as TextView?
        val batteryLevelFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        mBatteryLevelReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, i: Intent) {
                val level: Int = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                mWatchLevel = DecimalFormat("0.0")
                    .format((level.toFloat() / scale.toFloat() * 100.0f).toDouble()) + "%"
                sendMessage(this@MainActivity, mWatchLevel)
                updateUI()
            }
        }
        registerReceiver(mBatteryLevelReceiver, batteryLevelFilter)
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)
    }

    fun updateUI() {
        mWatch?.setText(mWatchLevel)
        mSmartphone?.setText(mSmartphoneLevel)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id: Int = item.getItemId()
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        if (mBatteryLevelReceiver != null) {
            unregisterReceiver(mBatteryLevelReceiver)
            mBatteryLevelReceiver = null
        }
        super.onDestroy()
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val message: String? = intent.getStringExtra("message")
            mSmartphoneLevel = message?:""
            updateUI()
        }
    }

    fun sendMessage(context: Context, param1: String?) {
        val intent = Intent(context, ListenerService::class.java)
        intent.setAction(ListenerService.ACTION_SM)
        intent.putExtra(ListenerService.ACTION_SM_PARAM, param1)
        context.startService(intent)
    }

    companion object {
        var mWatchLevel = "?"
        var mSmartphoneLevel = "?"
    }
}