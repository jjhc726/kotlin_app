package com.example.vistaquickdonation.ui.screens.charity

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.data.model.Charity
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.ui.theme.TealMedium
import com.example.vistaquickdonation.ui.theme.White
import com.example.vistaquickdonation.viewmodel.CharityViewModel

@Composable
fun CharityProfileScreen(
    charity: Charity,
    onBackClick: () -> Unit,
    viewModel: CharityViewModel = viewModel()
) {
    var liked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF80CBC4), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = charity.name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = charity.description,
            fontSize = 16.sp,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Current Campaigns",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            charity.campaigns.forEach {
                Text("• $it", fontSize = 16.sp, color = DeepBlue)
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Charity Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 Address: 123 Kindness Ave, Bogotá", color = DeepBlue, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("🕓 Hours: Mon–Fri, 8:00 AM – 6:00 PM", color = DeepBlue, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            // ❤️ Like
            IconButton(onClick = {
                liked = !liked
                viewModel.addInteraction()
            }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = if (liked) Color.Red else Color.Gray
                )
            }

            // 📤 Share
            IconButton(onClick = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${charity.name}\n\n${charity.description}\n📍 123 Kindness Ave, Bogotá\n🕓 Mon–Fri, 8:00 AM – 6:00 PM"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share charity"))
                viewModel.addInteraction()
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 💰 Botón Donar
        Button(
            onClick = {
                // Aquí se incrementa la interacción al donar
                viewModel.addInteraction()
                // Acción adicional (abrir link, diálogo, etc.)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
        ) {
            Text("Donate", color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = TealMedium)
        ) {
            Text("Volver", color = White)
        }
    }
}
