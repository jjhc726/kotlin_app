package com.example.Recyclothes.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.White
import com.example.Recyclothes.viewmodel.FavoriteCharitiesViewModel

@Composable
fun FavoriteCharitiesScreen(
    onOpenCharity: (Int) -> Unit
) {
    val vm: FavoriteCharitiesViewModel = viewModel()
    val ui by vm.ui.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .padding(16.dp)
    ) {
        Text("My Favorite Charities", color = TealDark, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (ui.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet.", color = TealDark)
            }
        } else {
            LazyColumn {
                items(ui.size) { index ->
                    val c = ui[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onOpenCharity(c.id) },
                        colors = CardDefaults.cardColors(containerColor = TealDark),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(c.name, color = White, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(c.description, color = White, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
