package com.example.Recyclothes.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.Recyclothes.data.local.CharityEntity
import com.example.Recyclothes.viewmodel.FavoriteStat
import com.example.Recyclothes.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    onOpenProfile: (CharityEntity) -> Unit,
    top: List<FavoriteStat>,
    nameResolver: (Int) -> String
) {
    val vm: FavoritesViewModel = viewModel()
    val favorites by vm.favorites.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {

        if (favorites.isNotEmpty()) {
            item {
                Text(
                    text = "Your favorites",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF174B61),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE9F4F8))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            items(favorites) { fav ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3B4C)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onOpenProfile(fav) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF22A39F))
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = fav.name,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = fav.description,
                                color = Color(0xFFBFE8F2),
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = { vm.toggleFavorite(fav.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Remove favorite",
                                tint = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFBFE8F2))
                Spacer(Modifier.height(8.dp))
            }
        }

        if (top.isNotEmpty()) {
            item {
                Text(
                    text = "⭐ Top 3 Favorited Charities",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF174B61),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE9F4F8))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(top) { index, stat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3B4C)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF22A39F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = nameResolver(stat.charityId),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${stat.count} users marked as favorite",
                                color = Color(0xFFBFE8F2),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
