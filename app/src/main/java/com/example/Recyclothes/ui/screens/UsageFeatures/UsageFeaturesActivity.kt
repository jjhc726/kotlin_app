package com.example.Recyclothes.ui.screens.usagefeatures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.screens.UsageFeatures.UsageFeaturesScreen
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.viewmodel.UsageFeaturesViewModel

class UsageFeaturesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draftId = intent.getStringExtra("DRAFT_ID")
        setContent {
            RecyclothesTheme {
                val vm: UsageFeaturesViewModel = viewModel()
                LaunchedEffect(draftId) { draftId?.let { vm.loadDraftById(it) } }
                UsageFeaturesScreen(vm = vm)
            }
        }
    }
}
