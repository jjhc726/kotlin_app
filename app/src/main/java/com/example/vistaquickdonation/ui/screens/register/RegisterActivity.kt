package com.example.vistaquickdonation.ui.screens.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vistaquickdonation.ui.screens.login.LoginActivity
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import com.example.vistaquickdonation.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VistaQuickDonationTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegister = {
                        lifecycleScope.launch {
                            val ok = viewModel.registerUser()
                            if (ok) {
                                viewModel.showToast(
                                    context = this@RegisterActivity,
                                    message = "Usuario registrado ✅"
                                )
                                viewModel.clearForm()
                                goToLogin()
                            } else {
                                viewModel.showToast(
                                    context = this@RegisterActivity,
                                    message = "Error al registrar ❌"
                                )
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
