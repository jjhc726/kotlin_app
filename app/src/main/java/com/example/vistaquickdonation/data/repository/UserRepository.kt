package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.remote.FirebaseUserService

class UserRepository(
    private val service: FirebaseUserService = FirebaseUserService()
) {
    suspend fun signIn(email: String, password: String): Boolean {
        return service.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return service.signUp(email, password)
    }

    fun currentEmail(): String? = service.currentEmail()

    fun signOut() = service.signOut()
}
