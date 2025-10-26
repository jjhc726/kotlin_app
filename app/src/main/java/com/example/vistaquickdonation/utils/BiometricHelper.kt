package com.example.vistaquickdonation.utils

import android.os.Build
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricHelper(private val activity: FragmentActivity) {

    fun canUseBiometricOrCredential(): Boolean {
        val authenticators =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            } else {
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            }

        return BiometricManager.from(activity)
            .canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(activity, errString, Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, "Fingerprint not recognized", Toast.LENGTH_SHORT).show()
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

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
