package com.example.Recyclothes.ui.screens.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val charityName = intent.getStringExtra("charityName") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""

        setContent {
            ChatScreen(
                charityName = charityName,
                userId = userId,
                onBack = { finish() }
            )
        }
    }
}
