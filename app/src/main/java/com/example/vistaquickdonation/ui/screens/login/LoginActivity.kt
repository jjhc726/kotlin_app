package com.example.vistaquickdonation.ui.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vistaquickdonation.ui.screens.home.HomePageActivity
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import com.example.vistaquickdonation.utils.BiometricHelper
import com.example.vistaquickdonation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val biometricHelper = BiometricHelper(this)

        setContent {
            VistaQuickDonationTheme {
                LoginScreen(
                    onLogin = { email, password ->
                        lifecycleScope.launch {
                            val success = viewModel.login(email, password)
                            if (success) goToHome()
                            else Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    },
                    supportsBiometric = biometricHelper.canUseBiometricOrCredential(),
                    onBiometricClick = { biometricHelper.showBiometricPrompt { goToHome() } }
                )
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomePageActivity::class.java))
        finish()
    }
}
