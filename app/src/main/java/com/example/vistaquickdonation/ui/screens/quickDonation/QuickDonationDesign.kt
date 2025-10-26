package com.example.vistaquickdonation.ui.screens.quickDonation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.screens.home.HomePageActivity
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.MediumBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuickDonationDesign(viewModel: DonationViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        viewModel.capturedImage.value = bitmap
    }

    // Scaffold needed so SnackbarHost can show snackbars
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MediumBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MediumBlue)
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Donate Your Clothing",
                fontSize = 26.sp,
                color = DeepBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(SoftBlue, shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                val capturedImage = viewModel.capturedImage.value
                if (capturedImage != null) {
                    Image(
                        bitmap = capturedImage.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Item Image (Required)", color = DeepBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Take Photo", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Description (Required)", color = DeepBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = DeepBlue,
                    unfocusedTextColor = DeepBlue
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.clothingType.value,
                onValueChange = { viewModel.clothingType.value = it },
                label = { Text("Clothing Type (Required)", color = DeepBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = DeepBlue,
                    unfocusedTextColor = DeepBlue
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.size.value,
                onValueChange = { viewModel.size.value = it },
                label = { Text("Size (Required)", color = DeepBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = DeepBlue,
                    unfocusedTextColor = DeepBlue
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.brand.value,
                onValueChange = { viewModel.brand.value = it },
                label = { Text("Brand (Required)", color = DeepBlue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = DeepBlue,
                    unfocusedTextColor = DeepBlue
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    viewModel.uploadDonation { success ->
                        if (success) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Donation stored successfully",
                                    withDismissAction = true
                                )
                            }

                            // Wait a bit so user sees the snackbar, then navigate
                            scope.launch {
                                delay(900)
                                val intent = Intent(context, HomePageActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                                (context as? Activity)?.finish()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Error saving the donation",
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepBlue,
                    contentColor = Color.White,
                    disabledContainerColor = DeepBlue.copy(alpha = 0.5f),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Submit Donation", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
