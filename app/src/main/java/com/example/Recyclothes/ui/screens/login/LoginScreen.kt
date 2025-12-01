package com.example.Recyclothes.ui.screens.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.screens.register.RegisterActivity
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealMedium

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    supportsBiometric: Boolean,
    onBiometricClick: () -> Unit,
    isOnline: Boolean,
    justLostConnection: Boolean,
    justRegainedConnection: Boolean
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showBanner by remember { mutableStateOf(false) }
    var bannerText by remember { mutableStateOf("") }

    // ---- Manejo de conexión ----
    LaunchedEffect(Unit) {
        if (!isOnline) {
            bannerText = "No tienes conexión a internet"
            showBanner = true
        }
    }

    LaunchedEffect(justLostConnection) {
        if (justLostConnection) {
            bannerText = "No tienes conexión a internet"
            showBanner = true
        }
    }

    LaunchedEffect(justRegainedConnection) {
        if (justRegainedConnection) {
            bannerText = "Conectado nuevamente"
            showBanner = true
            kotlinx.coroutines.delay(2500)
            if (isOnline) showBanner = false
        }
    }

    LaunchedEffect(isOnline) {
        if (isOnline) {
            bannerText = "Conectado nuevamente"
            showBanner = true
            kotlinx.coroutines.delay(2500)
            showBanner = false
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DeepBlue)
    ) {

        // ---- Banner superior ----
        androidx.compose.animation.AnimatedVisibility(
            visible = showBanner,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Box(
                Modifier
                    .background(
                        if (bannerText.contains("Conectado")) Color(0xFF00C853) else Color.Red,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(text = bannerText, color = Color.White)
            }
        }

        // ---- Contenido principal ----
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .background(
                    color = SoftBlue.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                "Welcome",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepBlue
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.DarkGray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // -------- BIOMETRÍA --------
            if (supportsBiometric) {
                OutlinedButton(
                    onClick = {
                        if (!isOnline) {
                            Toast.makeText(context, "No tienes internet", Toast.LENGTH_SHORT).show()
                        } else onBiometricClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBlue)
                ) {
                    Text("Sign in with fingerprint")
                }

                Spacer(Modifier.height(8.dp))
                Text("or", color = DeepBlue, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            // -------- BOTÓN LOGIN NORMAL --------
            Button(
                onClick = {
                    if (!isOnline) {
                        Toast.makeText(context, "No tienes internet", Toast.LENGTH_SHORT).show()
                    } else onLogin(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealMedium)
            ) {
                Text("Sign In", fontSize = 18.sp)
            }

            Spacer(Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account?", color = DeepBlue, fontSize = 12.sp)

                TextButton(
                    onClick = {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    }
                ) {
                    Text("Register Now", fontSize = 12.sp, color = DeepBlue)
                }
            }
        }
    }
}
