package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.vistaquickdonation.data.UserRepository
import com.example.vistaquickdonation.model.UserCredentials
import com.example.vistaquickdonation.ui.screens.RegisterScreen
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    private val repo = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VistaQuickDonationTheme {
                RegisterScreen(
                    onRegister = { email, password ->
                        lifecycleScope.launch {
                            val ok = repo.signUp(UserCredentials(email, password))
                            if (ok) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Usuario registrado ✅",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToLogin()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Error al registrar ❌",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onGoToLogin = { goToLogin() }
                )
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
