package com.example.vistaquickdonation.notification.service

import android.annotation.SuppressLint
import com.example.vistaquickdonation.notification.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "Recyclothes"
        val body = message.notification?.body ?: message.data["body"] ?: "You have a new message"

        NotificationHelper.showNotification(this, title, body)
    }
}
