package com.example.Recyclothes.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.Recyclothes.data.repository.UserRepository

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    val fullName = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val city = mutableStateOf("")

    val interests = mutableStateListOf<String>()

    fun toggleInterest(interest: String) {
        if (interests.contains(interest)) {
            interests.remove(interest)
        } else {
            interests.add(interest)
        }
    }

    fun validateInputs(): String? {
        if (fullName.value.isBlank()) return "El nombre no puede estar vacío"
        if (fullName.value.length > 40) return "El nombre es demasiado largo (máximo 40 caracteres)"

        if (email.value.isBlank()) return "El correo no puede estar vacío"
        if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) return "El correo no es válido"

        if (password.value.isBlank()) return "La contraseña no puede estar vacía"
        if (password.value.length < 6) return "La contraseña debe tener al menos 6 caracteres"

        if (city.value.isBlank()) return "La ciudad no puede estar vacía"

        if (interests.isEmpty()) return "Selecciona al menos un interés"

        return null
    }
    suspend fun registerUser(): Boolean {
        val interestsString = interests.joinToString(", ")
        return repository.signUp(
            name = fullName.value,
            email = email.value,
            password = password.value,
            city = city.value,
            interests = interestsString
        )
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun clearForm() {
        fullName.value = ""
        email.value = ""
        password.value = ""
        city.value = ""
        interests.clear()
    }
}
