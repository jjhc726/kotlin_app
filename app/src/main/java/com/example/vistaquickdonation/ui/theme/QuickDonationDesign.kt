package com.example.vistaquickdonation.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickDonationDesign() {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher para abrir la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        capturedImage = bitmap
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAFC7CA))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Donate Your Clothing",
            fontSize = 26.sp,
            color = Color(0xFF3E6F75)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let’s find the perfect new home for your pre-loved items.",
            fontSize = 14.sp,
            color = Color(0xFF6F9AA0),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📸 Caja para mostrar la imagen capturada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFBCEEF5), shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (capturedImage != null) {
                Image(
                    bitmap = capturedImage!!.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Item Image (Required)", color = Color(0xFF003137))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { cameraLauncher.launch(null) }) {
                        Text("Take Photo")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            InputBox("Description (Required)")
            Spacer(modifier = Modifier.height(12.dp))
            InputBox("Clothing Type (Required)")
            Spacer(modifier = Modifier.height(12.dp))
            InputBox("Size (Required)")
            Spacer(modifier = Modifier.height(12.dp))
            InputBox("Brand (Required)")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { /* TODO: Enviar datos */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF003137),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Submit Donation")
        }
    }
}

// 🔹 Reutilizo la caja blanca para los campos
@Composable
fun InputBox(placeholder: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White, shape = RoundedCornerShape(4.dp))
            .padding(start = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(placeholder, color = Color(0xFF6F9AA0), fontSize = 14.sp)
    }
}
