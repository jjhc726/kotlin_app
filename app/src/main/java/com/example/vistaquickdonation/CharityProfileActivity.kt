package com.example.vistaquickdonation

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.screens.CharityProfileDesign
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class CharityProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                CharityProfileDesign()
            }
        }
    }
}

