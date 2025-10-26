package com.example.vistaquickdonation.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.data.model.DonationItem
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.vistaquickdonation.ui.HomePageActivity

@Composable
fun QuickDonationDesign(viewModel: DonationViewModel) {

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }


    var description by remember { mutableStateOf("") }
    var clothingType by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }

    val context = LocalContext.current

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

        Spacer(modifier = Modifier.height(20.dp))


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


        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Required)") },
            colors = OutlinedTextFieldDefaults.colors()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = clothingType,
            onValueChange = { clothingType = it },
            label = { Text("Clothing Type (Required)") }
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = size,
            onValueChange = { size = it },
            label = { Text("Size (Required)") }
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text("Brand (Required)") }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val donation = DonationItem(
                    description = description,
                    clothingType = clothingType,
                    size = size,
                    brand = brand
                )
                viewModel.uploadDonation(donation) { success ->
                    if (success) {
                        println("Donation stored successfully")
                        val intent = Intent(context, HomePageActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    } else {
                        println("Error at saving the donation")
                    }
                }
            },
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
