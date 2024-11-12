package com.kotlity.core.notification.domain

interface PendingIntentProvider {

    fun getPendingIntent(): Any
}