package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.FirebaseUserService

class UserRepository(
    private val service: FirebaseUserService = FirebaseUserService()
) {
    suspend fun signIn(email: String, password: String): Boolean {
        return service.signIn(email, password)
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        city: String,
        interests: String
    ): Boolean {
        return service.signUp(name, email, password, city, interests)
    }

    fun currentEmail(): String? = service.currentEmail()

    fun signOut() = service.signOut()
}
