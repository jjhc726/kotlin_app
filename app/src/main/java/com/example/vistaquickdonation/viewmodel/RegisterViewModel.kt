package com.example.vistaquickdonation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.vistaquickdonation.data.repository.UserRepository

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    val fullName = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val city = mutableStateOf("")
    val interest = mutableStateOf("")

    suspend fun registerUser(): Boolean {
        return repository.signUp(email.value, password.value)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun clearForm() {
        fullName.value = ""
        email.value = ""
        password.value = ""
        city.value = ""
        interest.value = ""
    }
}
