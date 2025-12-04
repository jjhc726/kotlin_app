package com.example.Recyclothes.ui.screens.seasonalCampaigns

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.model.SeasonalCampaign
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealMedium
import com.example.Recyclothes.viewmodel.SeasonalCampaignsViewModel

@Composable
fun CampaignDetailScreen(
    campaign: SeasonalCampaign,
    onBackClick: () -> Unit,
    viewModel: SeasonalCampaignsViewModel = viewModel()
) {
    var liked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        coil.compose.AsyncImage(
            model = campaign.imageRes,
            contentDescription = campaign.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = campaign.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF004D40)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = campaign.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF004D40)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .background(Color(0xFF80CBC4), RoundedCornerShape(10.dp))
                )
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
                text = "Detalles del evento",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF004D40)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📅 Fecha: ${campaign.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF004D40)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📍 Lugar: ${campaign.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF004D40)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {

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

            IconButton(onClick = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${campaign.title}\n\n${campaign.description}\n📅 ${campaign.date}\n📍 ${campaign.location}"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Compartir campaña"))
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

        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = TealMedium)
        ) {
            Text(text = "Volver", color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
