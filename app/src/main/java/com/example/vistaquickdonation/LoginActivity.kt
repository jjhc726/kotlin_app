package com.example.vistaquickdonation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vistaquickdonation.model.UserRepository
import com.example.vistaquickdonation.ui.login.LoginScreen
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private val repo = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val canUseBiometric = canUseBiometricOrCredential()

        setContent {
            VistaQuickDonationTheme {
                LoginScreen(
                    onLogin = { email, password ->
                        lifecycleScope.launch {
                            val ok = repo.signIn(email, password)
                            if (ok) {
                                // Guarda el email de FirebaseAuth (fallback al escrito)
                                val authedEmail = repo.currentEmail() ?: email.trim().lowercase()
                                getSharedPreferences("session", MODE_PRIVATE)
                                    .edit {
                                        putString("email", authedEmail)
                                    }

                                goToHome()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid email or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    supportsBiometric = canUseBiometric,
                    onBiometricClick = { showBiometricPromptSafe() }
                )
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomePageActivity::class.java))
        finish()
    }

    private fun canUseBiometricOrCredential(): Boolean {
        val authenticators =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            } else {
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            }

        return BiometricManager.from(this)
            .canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPromptSafe() {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                goToHome()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    this@LoginActivity,
                    "Fingerprint not recognized",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val promptInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock with biometrics")
                    .setSubtitle("Use your fingerprint or device credential")
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()
            } else {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock with biometrics")
                    .setSubtitle("Use your fingerprint")
                    .setNegativeButtonText("Cancel")
                    .build()
            }

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }
}
