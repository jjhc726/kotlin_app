package com.example.vistaquickdonation.ui.screens.login

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.screens.register.RegisterActivity
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.ui.theme.TealMedium

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit, supportsBiometric: Boolean, onBiometricClick: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .background(
                    color = SoftBlue.copy(alpha = 0.9f), shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Welcome", style = MaterialTheme.typography.headlineMedium, color = DeepBlue
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.DarkGray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = DeepBlue,
                    focusedLabelColor = DeepBlue,
                    unfocusedLabelColor = DeepBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray
                )
            )

            Spacer(Modifier.height(16.dp))

            if (supportsBiometric) {
                OutlinedButton(
                    onClick = onBiometricClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DeepBlue
                    )
                ) {
                    Text("Sign in with fingerprint")
                }

                Spacer(Modifier.height(8.dp))
                Text("or", color = DeepBlue, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealMedium)
            ) {
                Text("Sign In", fontSize = 18.sp)
            }

            Spacer(Modifier.height(30.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Don't have an account?", color = DeepBlue, fontSize = 12.sp)
                }

                Column {
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context, RegisterActivity::class.java
                                )
                            )
                        },
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Text("Register Now", fontSize = 12.sp, color = DeepBlue)
                    }
                }
            }


        }
    }
}
