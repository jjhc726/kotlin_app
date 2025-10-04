package com.example.vistaquickdonation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessaging

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannelIfNeeded()
        // Suscripción de prueba a un tópico general (puedes quitarlo si no lo usarás)
        FirebaseMessaging.getInstance().subscribeToTopic("global")
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "general",
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications for Recyclothes"
            }
            val nm = getSystemService<NotificationManager>()
            nm?.createNotificationChannel(channel)
        }
    }
}
