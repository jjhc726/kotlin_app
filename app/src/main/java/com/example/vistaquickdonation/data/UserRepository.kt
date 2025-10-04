package com.example.vistaquickdonation.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.vistaquickdonation.model.UserCredentials
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = db.collection("users")


    suspend fun signUp(user: UserCredentials): Boolean {
        return try {
            val docId = user.email.trim().lowercase()
            users.document(docId)
                .set(user.copy(email = docId))
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            val key = email.trim().lowercase()


            val doc = users.document(key).get().await()
            if (doc.exists()) {
                val stored: UserCredentials? = doc.toObject<UserCredentials>()
                return stored?.password == password
            }


            val snap = users
                .whereEqualTo("email", key)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .await()

            !snap.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun userExists(email: String): Boolean {
        return try {
            users.document(email.trim().lowercase()).get().await().exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
