package com.example.vistaquickdonation.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.data.repository.UserRepository
import com.example.vistaquickdonation.ui.screens.quickDonation.QuickDonationActivity
import com.example.vistaquickdonation.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import com.example.vistaquickdonation.viewmodel.NotificationsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val userRepo = remember { UserRepository() }

    val donationVM = viewModel<DonationViewModel>()
    val topDonors by donationVM.topDonors.collectAsState()

    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val lastDonationText by notificationsVM.lastDonationText.collectAsState()

    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        donationVM.loadTopDonors()
        userRepo.currentEmail()?.let { notificationsVM.start(it) }
    }

    fun go(target: Class<*>) = context.startActivity(Intent(context, target))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeContent(
            topDonors = topDonors,
            lastDonationText = lastDonationText,
            onQuickDonate = { go(QuickDonationActivity::class.java) },
            onSchedule = { go(ScheduleDonationActivity::class.java) }
        )
    }

    val showNotifications = notifications.isNotEmpty()
    if (showNotifications) {
        NotificationsSheet(
            sheetState = notifSheetState,
            onDismiss = { /* nada */ },
            notifications = notifications
        )
    }
}
