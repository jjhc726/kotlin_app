package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import com.example.Recyclothes.data.repository.UserRepository

class LoginViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    suspend fun login(email: String, password: String): Boolean {
        return repository.signIn(email, password)
    }
}