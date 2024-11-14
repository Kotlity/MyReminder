package com.kotlity.core.notification.domain

interface NotificationService {

    fun sendNotification(id: Int, title: String)
}