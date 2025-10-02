package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
                    if (email == "admin@test.com" && password == "1234") {
                        // 👉 Ahora lleva a HomePageActivity
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                        finish() // opcional: evita volver al login con "atrás"
                    } else {
                        Toast.makeText(
                            this,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
