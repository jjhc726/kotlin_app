package com.example.Recyclothes.ui.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.Recyclothes.ui.screens.main.MainNavigationActivity
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.utils.BiometricHelper
import com.example.Recyclothes.viewmodel.LoginViewModel
import com.example.Recyclothes.viewmodel.NetworkViewModel

import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private val networkViewModel: NetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val biometricHelper = BiometricHelper(this)

        setContent {
            RecyclothesTheme {

                val online by networkViewModel.isOnline.collectAsState()
                val lost by networkViewModel.justLostConnection.collectAsState()
                val regained by networkViewModel.justRegainedConnection.collectAsState()

                LoginScreen(
                    onLogin = { email, password ->
                        lifecycleScope.launch {
                            val success = viewModel.login(email, password)
                            if (success) goToHome()
                            else Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    },
                    supportsBiometric = biometricHelper.canUseBiometricOrCredential(),
                    onBiometricClick = {
                        if (!online) {
                            Toast.makeText(this, "No tienes internet", Toast.LENGTH_SHORT).show()
                        } else biometricHelper.showBiometricPrompt { goToHome() }
                    },
                    isOnline = online,
                    justLostConnection = lost,
                    justRegainedConnection = regained
                )
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, MainNavigationActivity::class.java))
        finish()
    }
}