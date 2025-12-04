package com.example.Recyclothes.data.repository

import android.content.Context
import androidx.room.Room
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.MessageEntity
import com.example.Recyclothes.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository(context: Context) {

    private val dbRoom = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "local_messages.db"
    ).build()

    private val messageDao = dbRoom.messageDao()

    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    private fun chatDocumentId(userEmail: String, charityName: String): String {
        val clean = userEmail.replace(".", "_")
        return "${clean}_${charityName}"
    }

    fun getMessagesFlow(charityName: String, userEmail: String): Flow<List<Message>> =
        callbackFlow {
            val docId = chatDocumentId(userEmail, charityName)

            val ref = chatsCollection
                .document(docId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)

            val listener = ref.addSnapshotListener { snapshot, _ ->
                val msgs = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()

                trySend(msgs)
            }

            awaitClose { listener.remove() }
        }

    /** ─────────────────────────────────────────────
     *  ENVÍO NORMAL SI HAY INTERNET
     *  SI NO: GUARDAR LOCALMENTE
     *  ───────────────────────────────────────────── */
    suspend fun sendMessage(charityName: String, userEmail: String, text: String, isOnline: Boolean): Message {

        val msgId = System.currentTimeMillis().toString()
        val timestamp = System.currentTimeMillis()

        if (!isOnline) {
            // Guardarlo localmente
            messageDao.insert(
                MessageEntity(
                    id = msgId,
                    charityName = charityName,
                    userEmail = userEmail,
                    text = text,
                    timestamp = timestamp,
                    needsResend = true
                )
            )

            return Message(
                id = msgId,
                fromUserId = userEmail,
                toCharityId = charityName,
                text = text,
                timestamp = timestamp,
                status = "error" // mostrar ⚠
            )
        }

        return sendToFirestore(msgId, charityName, userEmail, text, timestamp)
    }

    /** Enviar realmente a Firestore */
    private suspend fun sendToFirestore(
        id: String,
        charity: String,
        email: String,
        text: String,
        timestamp: Long
    ): Message {

        val docId = chatDocumentId(email, charity)

        val msg = Message(
            id = id,
            fromUserId = email,
            toCharityId = charity,
            text = text,
            timestamp = timestamp,
            status = "sending"
        )

        val docRef = chatsCollection
            .document(docId)
            .collection("messages")
            .document(id)

        docRef.set(msg).await()

        return try {
            docRef.update("status", "sent").await()
            msg.copy(status = "sent")
        } catch (e: Exception) {
            msg.copy(status = "error")
        }
    }

    suspend fun resendPendingMessages() {
        val list = messageDao.getPendingMessages()

        list.forEach { localMsg ->
            try {
                sendToFirestore(
                    id = localMsg.id,
                    charity = localMsg.charityName,
                    email = localMsg.userEmail,
                    text = localMsg.text,
                    timestamp = localMsg.timestamp
                )
                messageDao.delete(localMsg.id)
            } catch (_: Exception) { }
        }
    }
}
