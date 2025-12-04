package com.example.Recyclothes.ui.screens.quickDonation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.viewmodel.DonationViewModel

@Composable
fun DonationImagePicker(viewModel: DonationViewModel) {

    val context = LocalContext.current

    // Cargar imagen guardada
    LaunchedEffect(Unit) {
        viewModel.reloadSavedImage()
    }

    val imageUri = viewModel.capturedImageUri.collectAsState().value
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // =====================================================
    // 1️⃣ LAUNCHER DE CÁMARA (TakePicture)
    // =====================================================

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            viewModel.updateImageUri(tempImageUri!!)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            val savedUri = viewModel.copyGalleryImageToAppStorage(context, selectedUri)
            viewModel.updateImageUri(savedUri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MediumBlue, RoundedCornerShape(12.dp))
            .border(1.dp, DeepBlue, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {

        if (imageUri != null) {

            val bitmap = viewModel.uriToBitmap(context, imageUri)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Botón para eliminar
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(32.dp)
                    .background(DeepBlue, RoundedCornerShape(50))
                    .clickable { viewModel.updateImageUri(null) },
                contentAlignment = Alignment.Center
            ) {
                Text("X", color = MediumBlue, fontSize = 18.sp)
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text("Select Image Source", fontSize = 16.sp, color = DeepBlue)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // 📸 CÁMARA (SIN PERMISOS)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                tempImageUri = viewModel.createImageUri(context)
                                tempImageUri?.let { cameraLauncher.launch(it) }
                            }
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = DeepBlue
                        )
                        Text("Camera", color = DeepBlue)
                    }

                    // 🖼️ GALERÍA (SIN PERMISOS)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                galleryLauncher.launch("image/*")
                            }
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = DeepBlue
                        )
                        Text("Gallery", color = DeepBlue)
                    }
                }
            }
        }
    }
}