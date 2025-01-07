package com.kotlity.myreminder

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kotlity.core.Is24HourFormatReceiver

class MainActivity : ComponentActivity() {

    private var is24HourFormatReceiver: Is24HourFormatReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerIs24HourFormatReceiver()
        enableEdgeToEdge()
        setContent {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterIs24HourFormatReceiver()
    }

    private fun registerIs24HourFormatReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)
        is24HourFormatReceiver = Is24HourFormatReceiver().apply { registerReceiver(this, intentFilter) }
    }

    private fun unregisterIs24HourFormatReceiver() {
        is24HourFormatReceiver?.let { unregisterReceiver(it) }
    }
}