package com.kotlity.core.notification.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kotlity.core.notification.domain.NotificationService
import com.kotlity.core.notification.domain.PendingIntentProvider

class DefaultNotificationService(
    private val notificationManager: NotificationManager,
    private val context: Context
): NotificationService {

    override fun sendNotification(
        id: Int,
        title: String,
        pendingIntentProvider: PendingIntentProvider
    ) {
        val smallIcon = androidx.core.R.drawable.notification_bg
        val contentText = context.getString(com.kotlity.core.resources.R.string.notificationContentText)
        val priority = NotificationCompat.PRIORITY_HIGH
        val pendingIntent = pendingIntentProvider.getPendingIntent() as PendingIntent
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, context.getString(com.kotlity.core.resources.R.string.notificationChannelId))
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