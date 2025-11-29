package com.example.Recyclothes.ui.screens.quickDonation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.White
import com.example.Recyclothes.utils.UsageTracker
import com.example.Recyclothes.viewmodel.DonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickDonationDesign(viewModel: DonationViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        UsageTracker.bump(FeatureId.QUICK_DONATION_OPEN)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Quick Donation", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TealDark
                )
            )
        },
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
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.saveDraft()
                    android.widget.Toast
                        .makeText(context, "Draft saved locally.", android.widget.Toast.LENGTH_LONG)
                        .show()

                    (context as? android.app.Activity)?.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .height(50.dp)
            ) {
                Text("Save Draft", fontSize = 18.sp)
            }
        }
    }
}
