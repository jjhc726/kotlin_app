package com.example.Recyclothes.ui.screens.quickDonation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.viewmodel.DonationViewModel

@Composable
fun DonationImagePicker(viewModel: DonationViewModel) {

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        viewModel.updateImage(bitmap)   // <--- FIX AQUI
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MediumBlue, shape = RoundedCornerShape(12.dp))
            .border(color = DeepBlue, width = 1.dp, shape = RoundedCornerShape(12.dp))
            .clickable { cameraLauncher.launch(null) },
        contentAlignment = Alignment.Center
    ) {
        val capturedImage = viewModel.capturedImage.collectAsState().value
        if (capturedImage != null) {
            Image(
                bitmap = capturedImage.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Icon",
                    tint = DeepBlue,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Item Image (Required)", color = DeepBlue, fontSize = 15.sp, fontWeight = Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Touch to open the camera", color = DeepBlue.copy(alpha = 0.8f), fontSize = 13.sp)
            }
        }
    }
}
