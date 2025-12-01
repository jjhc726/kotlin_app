package com.example.Recyclothes.ui.screens.favorites

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.local.CharityEntity
import com.example.Recyclothes.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onOpenProfile: (CharityEntity) -> Unit,
) {
    val vm: FavoritesViewModel = viewModel()
    val favorites by vm.favorites.collectAsState(initial = emptyList())
    val top by vm.top3.collectAsState(initial = emptyList())
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

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

        if (top.isNotEmpty()) {
            item { Spacer(Modifier.height(24.dp)) }
            item { HorizontalDivider(thickness = 1.dp, color = Color(0xFFBFE8F2)) }
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Text(
                    text = "Top 3 Favorited Charities",
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
                            Text("#${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            var name by remember(stat.charityId) { mutableStateOf("Charity #${stat.charityId}") }
                            LaunchedEffect(stat.charityId) {
                                name = vm.resolveName(stat.charityId)
                            }
                            Text(name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text("${stat.count} users marked as favorite", color = Color(0xFFBFE8F2), fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            Button(
                onClick = { (ctx as? Activity)?.finish() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) { Text("Back to Home") }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}
