package com.kotlity.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kotlity.core.resources.R

const val NOTIFICATION_ACTION = "com.kotlity.myreminder.action.ACTION_OPEN_FROM_NOTIFICATION"

class DefaultNotificationService(
    private val notificationManager: NotificationManager,
    private val context: Context
): NotificationService {

    override fun sendNotification(id: Int, title: String) {
        val smallIcon = androidx.core.R.drawable.notification_bg
        val contentText = context.getString(R.string.notificationContentText)
        val priority = NotificationCompat.PRIORITY_HIGH
        val intent = Intent(NOTIFICATION_ACTION).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, context.getString(R.string.notificationChannelId))
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(priority)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(priority)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(id, notificationBuilder.build())
    }
}