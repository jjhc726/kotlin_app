package com.example.vistaquickdonation.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = db.collection("users")

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseUserService", "signIn() failed", e)
            false
        }
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        city: String,
        interests: String
    ): Boolean {
        val key = email.trim().lowercase()
        return try {
            auth.createUserWithEmailAndPassword(key, password).await()

            val profile = mapOf(
                "name" to name,
                "email" to key,
                "city" to city,
                "interests" to interests,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            users.document(key).set(profile).await()

            true
        } catch (e: Exception) {
            Log.e("FirebaseUserService", "signUp() failed", e)
            false
        }
    }

    fun currentEmail(): String? = auth.currentUser?.email?.trim()?.lowercase()

    fun signOut() = auth.signOut()
}
