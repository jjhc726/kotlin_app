package com.example.vistaquickdonation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegister: (String, String) -> Unit,   // ✅ nuevo
    onGoToLogin: () -> Unit                 // ✅ para navegar a login
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Crear Cuenta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TealDark,
                        titleContentColor = AquaLight
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Únete a la red de donadores y voluntarios",
                    color = DeepBlue,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TealMedium),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RegisterTextField(fullName, { fullName = it }, "Nombre completo")
                        Spacer(Modifier.height(16.dp))

                        RegisterTextField(email, { email = it }, "Correo electrónico")
                        Spacer(Modifier.height(16.dp))

                        RegisterTextField(password, { password = it }, "Contraseña", password = true)
                        Spacer(Modifier.height(16.dp))

                        RegisterTextField(city, { city = it }, "Ciudad")
                        Spacer(Modifier.height(16.dp))

                        RegisterTextField(interest, { interest = it }, "Intereses (Donar, Voluntariado...)")
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { onRegister(email, password) },   // ✅ conecta con la lógica de repo
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealMedium)
                ) {
                    Text(
                        "Registrarse",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AquaLight
                    )
                }

                Spacer(Modifier.height(20.dp))

                TextButton(onClick = { onGoToLogin() }) {   // ✅ vuelve al login
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = TealDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    password: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, color = DeepBlue, fontSize = 14.sp)
        },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = TealMedium,
            unfocusedIndicatorColor = MediumBlue,
            cursorColor = TealMedium,
            focusedLabelColor = DeepBlue,
            unfocusedLabelColor = DeepBlue,
            focusedTextColor = DeepBlue,
            unfocusedTextColor = DeepBlue
        )
    )
}
