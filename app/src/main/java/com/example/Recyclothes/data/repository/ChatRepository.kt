package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    private fun chatDocumentId(userEmail: String, charityName: String): String {
        val cleanEmail = userEmail.replace(".", "_")
        return "${cleanEmail}_${charityName}"
    }

    fun getMessagesFlow(charityName: String, userEmail: String): Flow<List<Message>> =
        callbackFlow {
            val docId = chatDocumentId(userEmail, charityName)

            val ref = chatsCollection
                .document(docId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)

            val listener = ref.addSnapshotListener { snapshot, _ ->
                val msgs = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }
                    ?: emptyList()
                trySend(msgs)
            }

            awaitClose { listener.remove() }
        }

    suspend fun sendMessage(charityName: String, userEmail: String, text: String) {
        val docId = chatDocumentId(userEmail, charityName)

        val msg = Message(
            id = System.currentTimeMillis().toString(),
            fromUserId = userEmail,
            toCharityId = charityName,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        chatsCollection
            .document(docId)
            .collection("messages")
            .document(msg.id)
            .set(msg)
            .await()
    }
}
