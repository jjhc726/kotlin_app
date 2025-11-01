package com.example.Recyclothes.ui.screens.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.Recyclothes.ui.screens.login.LoginActivity
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecyclothesTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegister = {
                        val validationError = viewModel.validateInputs()
                        if (validationError != null) {
                            viewModel.showToast(this, validationError)
                        } else {
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
                                        message = "Error al registrar ❌ Intenta nuevamente."
                                    )
                                }
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