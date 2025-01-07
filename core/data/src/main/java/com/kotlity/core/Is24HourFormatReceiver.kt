package com.kotlity.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import com.kotlity.TimeFormatter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Is24HourFormatReceiver: BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        if (!isShouldHandleReceiver(intent = intent)) return

        val timeFormatter by inject<TimeFormatter>()
        val is24HourFormat = DateFormat.is24HourFormat(context)

        timeFormatter.is24HourFormatChanged(update = is24HourFormat)
    }

    private fun isShouldHandleReceiver(intent: Intent) = intent.action == Intent.ACTION_TIME_CHANGED

}