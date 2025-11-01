package com.example.Recyclothes.ui.screens.home

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.ui.screens.quickDonation.QuickDonationActivity
import com.example.Recyclothes.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.utils.NetworkObserver
import com.example.Recyclothes.viewmodel.DonationViewModel
import com.example.Recyclothes.viewmodel.NotificationsViewModel


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
    var showNotifications by remember { mutableStateOf(false) }
    var firstLoad by remember { mutableStateOf(true) }
    val observer = remember { NetworkObserver(context) }

    DisposableEffect(Unit) {
        val callback = observer.registerCallback(
            onAvailable = {  },
            onLost = {  }
        )
        onDispose {
            observer.unregisterCallback(callback)
        }
    }

    LaunchedEffect(Unit) {
        donationVM.loadTopDonors()
        userRepo.currentEmail()?.let { notificationsVM.start(it) }
    }

    LaunchedEffect(notifications) {
        if (!firstLoad && notifications.isNotEmpty()) {
            showNotifications = true
        }
        if (firstLoad) {
            firstLoad = false
        }
    }

    fun go(target: Class<*>) = context.startActivity(Intent(context, target))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .padding(horizontal = 20.dp)
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

    if (showNotifications) {
        NotificationsSheet(
            sheetState = notifSheetState,
            onDismiss = { showNotifications = false },
            notifications = notifications
        )
    }
}

