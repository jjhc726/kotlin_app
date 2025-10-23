package com.example.vistaquickdonation.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val users = db.collection("users")


    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "signIn() failed", e)
            false
        }
    }


    suspend fun signUp(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        return try {
            auth.createUserWithEmailAndPassword(key, password).await()

            val profile = mapOf("email" to key)
            users.document(key).set(profile).await()

            true
        } catch (e: Exception) {
            Log.e("UserRepository", "signUp() failed", e)
            false
        }
    }

    fun currentEmail(): String? = auth.currentUser?.email?.trim()?.lowercase()
}
