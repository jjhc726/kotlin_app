package com.example.Recyclothes.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.theme.AquaLight
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.TealMedium
import com.example.Recyclothes.viewmodel.RegisterViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    isOnline: Boolean,
    justLostConnection: Boolean,
    justRegainedConnection: Boolean,
    onRegister: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val fullName = viewModel.fullName.value
    val email = viewModel.email.value
    val password = viewModel.password.value
    val city = viewModel.city.value
    val selectedInterests = viewModel.interests

    var showBanner by remember { mutableStateOf(false) }
    var bannerText by remember { mutableStateOf("") }

    // ---- MANEJO DE CONEXIÓN (FUNCIONA IGUAL QUE LOGIN) ----

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

    // --------------------------------------------------------

    Scaffold(
        topBar = {
            Column {
                // 🔥🔥🔥 AHORA EL BANNER ESTÁ DENTRO DEL SCAFFOLD
                androidx.compose.animation.AnimatedVisibility(
                    visible = showBanner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(
                                if (bannerText.contains("Conectado")) Color(0xFF00C853) else Color.Red,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(text = bannerText, color = Color.White)
                    }
                }

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
            }
        },
        containerColor = SoftBlue
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
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RegisterTextField(fullName, { viewModel.fullName.value = it }, "Nombre completo")
                    Spacer(Modifier.height(16.dp))
                    RegisterTextField(email, { viewModel.email.value = it }, "Correo electrónico")
                    Spacer(Modifier.height(16.dp))
                    RegisterTextField(password, { viewModel.password.value = it }, "Contraseña", password = true)
                    Spacer(Modifier.height(16.dp))
                    RegisterTextField(city, { viewModel.city.value = it }, "Ciudad")

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Selecciona tus intereses",
                        color = DeepBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    InterestChips(
                        selectedInterests = selectedInterests,
                        onToggleInterest = { viewModel.toggleInterest(it) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onRegister,
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

            TextButton(onClick = onGoToLogin) {
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


@Composable
private fun InterestChips(
    selectedInterests: List<String>,
    onToggleInterest: (String) -> Unit
) {
    val options = listOf("Donar", "Voluntariado")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = selectedInterests.contains(option)
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) DeepBlue else Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onToggleInterest(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else DeepBlue,
                    fontWeight = FontWeight.Medium
                )
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
    val hasText = value.isNotEmpty()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                fontSize = 14.sp,
                color = if (hasText) AquaLight else DeepBlue
            )
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
            focusedTextColor = DeepBlue,
            unfocusedTextColor = DeepBlue,
            focusedLabelColor = AquaLight
        )
    )
}


