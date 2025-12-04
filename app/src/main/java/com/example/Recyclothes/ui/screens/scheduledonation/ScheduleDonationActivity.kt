package com.example.Recyclothes.ui.screens.scheduledonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.viewmodel.ScheduleDonationViewModel


class ScheduleDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draftId = intent.getStringExtra("DRAFT_ID")
        setContent {
            RecyclothesTheme {
                val vm: ScheduleDonationViewModel = viewModel()
                LaunchedEffect(draftId) { draftId?.let { vm.loadDraftById(it) } }
                ScheduleDonationDesign(vm = vm)
            }
        }
    }
}
