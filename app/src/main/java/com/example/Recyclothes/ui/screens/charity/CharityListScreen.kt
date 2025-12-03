package com.example.Recyclothes.ui.screens.charity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.White
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.Recyclothes.connectivity.ConnectivityBanner
import com.example.Recyclothes.viewmodel.CharityListFavoritesStateViewModel


@Composable
fun CharityListScreen(
    charities: List<Charity>,
    onCharityClick: (Charity) -> Unit
) {
    val ctx = LocalContext.current
    val observer = remember { com.example.Recyclothes.connectivity.ConnectivityObserver(ctx) }
    val onlineFlow = remember { observer.onlineFlow() }
    val onlineState by onlineFlow.collectAsState(initial = observer.isOnlineNow())

    val favVm: CharityListFavoritesStateViewModel = viewModel()
    val favoriteIds by favVm.favoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .padding(16.dp)
    ) {
        ConnectivityBanner(online = onlineState)
        LazyColumn {
            items(charities.size) { index ->
                val charity = charities[index]
                val isFav = favoriteIds.contains(charity.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onCharityClick(charity) },
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
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftBlue)
                                .clickable { onCharityClick(charity) }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column (
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCharityClick(charity) }
                        ) {
                            Text(
                                charity.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                charity.description,
                                fontSize = 16.sp,
                                color = White
                            )
                        }
                        IconButton(onClick = { favVm.toggle(charity.id) }) {
                            if (isFav) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Unfavorite", tint = Color.Red)
                            } else {
                                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorite", tint = White)
                            }
                        }
                    }
                }
            }
        }
    }
}
