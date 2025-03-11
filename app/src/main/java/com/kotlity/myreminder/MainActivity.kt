package com.kotlity.myreminder

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.kotlity.core.Is24HourFormatReceiver
import com.kotlity.core.ui.theme.MyReminderTheme

class MainActivity : ComponentActivity() {

    private var is24HourFormatReceiver: Is24HourFormatReceiver? = null

    private val onNewIntentViewModel by viewModels<OnNewIntentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerIs24HourFormatReceiver()
        enableEdgeToEdge()
        setContent {
            MyReminderTheme {
                MyReminderApp(
                    shouldNavigateToRemindersScreen = onNewIntentViewModel.shouldNavigateToRemindersScreenFlow
                )
            }
        }

        navigateToRemindersScreenIfNeeded(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navigateToRemindersScreenIfNeeded(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterIs24HourFormatReceiver()
    }

    private fun navigateToRemindersScreenIfNeeded(intent: Intent) {
        onNewIntentViewModel.navigateToRemindersScreenIfNeeded(action = intent.action)
    }

    private fun registerIs24HourFormatReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)
        is24HourFormatReceiver = Is24HourFormatReceiver().apply { registerReceiver(this, intentFilter) }
    }

    private fun unregisterIs24HourFormatReceiver() {
        is24HourFormatReceiver?.let { unregisterReceiver(it) }
    }
}