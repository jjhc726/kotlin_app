package com.example.vistaquickdonation.ui.screens.interactiveMap

import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Donation Map") },
        modifier = Modifier.height(100.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TealDark,
            titleContentColor = White
        )
    )
}
