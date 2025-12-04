package com.example.Recyclothes.ui.screens.seasonalCampaigns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.White
import com.example.Recyclothes.viewmodel.SeasonalCampaignsViewModel
import com.example.Recyclothes.ui.screens.seasonalCampaigns.components.OfflineBanner

@Composable
fun SeasonalCampaignsScreen(
    viewModel: SeasonalCampaignsViewModel = viewModel()
) {

    val campaigns = viewModel.campaigns.collectAsState().value
    val selectedCampaign = viewModel.selectedCampaign.collectAsState().value

    val networkAvailable = viewModel.networkStatus.collectAsState().value
    val bannerDismissed = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startNetworkObserver()
    }

    Box(modifier = Modifier.fillMaxSize()) {

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

                                coil.compose.AsyncImage(
                                    model = campaign.imageRes,
                                    contentDescription = campaign.title,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = campaign.title,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = campaign.description, color = White)
                                }
                            }
                        }
                    }
                }
            }

        } else {
            CampaignDetailScreen(
                campaign = selectedCampaign,
                onBackClick = { viewModel.goBack() }
            )
        }

        AnimatedVisibility(
            visible = !networkAvailable && !bannerDismissed.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .padding(12.dp)
        ) {
            OfflineBanner { bannerDismissed.value = true }
        }
    }
}
