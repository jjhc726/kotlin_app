package com.example.Recyclothes.ui.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                Surface(modifier = Modifier, color = SoftBlue) {
                    HomePageScreen()
                }
            }
        }
    }
}
