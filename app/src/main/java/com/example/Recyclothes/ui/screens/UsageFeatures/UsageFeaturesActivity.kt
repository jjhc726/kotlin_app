package com.example.Recyclothes.ui.screens.usagefeatures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.ui.screens.UsageFeatures.UsageFeaturesScreen
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class UsageFeaturesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                UsageFeaturesScreen()
            }
        }
    }
}
