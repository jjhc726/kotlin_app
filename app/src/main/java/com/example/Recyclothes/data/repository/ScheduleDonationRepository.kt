package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.ScheduledDonationDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Date

class ScheduleDonationRepository {
    private val col = Firebase.firestore.collection("scheduled_donations")

    suspend fun create(
        dto: ScheduledDonationDto,
        requestId: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = hashMapOf(
                "title"        to dto.title,
                "dateMillis"   to dto.dateMillis,
                "date"         to Timestamp(Date(dto.dateMillis)),
                "timeText"     to dto.timeText,
                "note"         to dto.note,
                "clothingType" to dto.clothingType,
                "size"         to dto.size,
                "brand"        to dto.brand,
                "userEmail"    to dto.userEmail,
                "createdAt"    to Timestamp.now()
            )

            val ok = withTimeoutOrNull(5_000) {
                if (requestId == null) {
                    col.add(payload).await()
                } else {
                    col.document(requestId).set(payload, SetOptions.merge()).await()
                }
                true
            }
            ok ?: false
        } catch (_: Exception) {
            false
        }
    }
}
