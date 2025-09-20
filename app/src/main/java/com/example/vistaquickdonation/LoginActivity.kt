package com.example.vistaquickdonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.vistaquickdonation.ui.login.LoginScreen
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VistaQuickDonationTheme {
                LoginScreen { email, password ->
                    // Aquí podrías validar credenciales o navegar
                    // Por ahora solo imprime:
                    println("Login con $email / $password")
                }
            }
        }
    }
}
