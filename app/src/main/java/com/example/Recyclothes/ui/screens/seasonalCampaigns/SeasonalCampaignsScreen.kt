package com.example.Recyclothes.ui.screens.seasonalCampaigns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.White
import com.example.Recyclothes.viewmodel.SeasonalCampaignsViewModel

@Composable
fun SeasonalCampaignsScreen(
    viewModel: SeasonalCampaignsViewModel = viewModel()
) {
    val campaigns = viewModel.campaigns.collectAsState().value
    val selectedCampaign = viewModel.selectedCampaign.collectAsState().value

    if (selectedCampaign == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBlue)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(campaigns.size) { index ->
                    val campaign = campaigns[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { viewModel.selectCampaign(campaign) },
                        colors = CardDefaults.cardColors(containerColor = TealDark),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(SoftBlue, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = campaign.title,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = campaign.description,
                                    color = White
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // 🔹 Se cambia el parámetro: ahora recibe directamente el objeto campaign
        CampaignDetailScreen(
            campaign = selectedCampaign,
            onBackClick = { viewModel.goBack() }
        )
    }
}
