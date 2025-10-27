package com.example.vistaquickdonation.ui.screens.quickDonation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.MediumBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.viewmodel.DonationViewModel

@Composable
fun QuickDonationDesign(viewModel: DonationViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MediumBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBlue)
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Donate Your Clothing", fontSize = 26.sp, color = DeepBlue)
            Text("Let's find the perfect new home for your items", fontSize = 15.sp, color = TealDark)
            Spacer(modifier = Modifier.height(20.dp))

            DonationImagePicker(viewModel)
            Spacer(modifier = Modifier.height(20.dp))

            DonationForm(viewModel)
            Spacer(modifier = Modifier.height(20.dp))

            Text("Tags", fontSize = 18.sp, color = DeepBlue, fontWeight = Bold)
            Spacer(modifier = Modifier.height(8.dp))
            DonationTagsSection(viewModel)
            Spacer(modifier = Modifier.height(28.dp))

            DonationSubmitButton(viewModel)
        }
    }
}
