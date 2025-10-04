package com.example.vistaquickdonation.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = db.collection("users")

    suspend fun signIn(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        return try {
            // 1) Intento por ID = email
            val docSnap = users.document(key).get().await()
            if (docSnap.exists()) {
                val storedPass = docSnap.getString("password")
                return storedPass == password
            }

            // 2) Fallback: solo por email (sin índice compuesto)
            val snap = users.whereEqualTo("email", key).limit(1).get().await()
            if (!snap.isEmpty) {
                val first = snap.documents.first()
                val storedPass = first.getString("password")
                return storedPass == password
            }

            false
        } catch (e: Exception) {
            Log.e("UserRepository", "signIn() failed", e)
            false
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        return try {
            users.document(key).set(
                mapOf(
                    "email" to key,
                    "password" to password
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "signUp() failed", e)
            false
        }
    }

    suspend fun userExists(email: String): Boolean {
        val key = email.trim().lowercase()
        return try {
            users.document(key).get().await().exists()
        } catch (e: Exception) {
            Log.e("UserRepository", "userExists() failed", e)
            false
        }
    }
}
