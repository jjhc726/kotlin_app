package com.example.vistaquickdonation.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.data.repository.UserRepository
import com.example.vistaquickdonation.ui.screens.quickDonation.QuickDonationActivity
import com.example.vistaquickdonation.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import com.example.vistaquickdonation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val donationVM = viewModel<DonationViewModel>()
    val topDonors by donationVM.topDonors.collectAsState()

    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val lastDonationText by notificationsVM.lastDonationText.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }
    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val userRepo = remember { UserRepository() }
    val sessionEmail = remember { userRepo.currentEmail() }

    LaunchedEffect(Unit) {
        donationVM.loadTopDonors()
        sessionEmail?.let { notificationsVM.start(it) }
    }

    fun go(target: Class<*>) = context.startActivity(Intent(context, target))

    val drawerTextColor = Color(0xFF003137)
    val drawerBgColor = Color.White

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer(
                drawerTextColor = drawerTextColor,
                drawerBgColor = drawerBgColor,
                context = context,
                scope = scope,
                drawerState = drawerState,
                userRepo = userRepo
            )
        }
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftBlue)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHeader(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationsClick = { showNotifications = true }
                )

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
    }
}
