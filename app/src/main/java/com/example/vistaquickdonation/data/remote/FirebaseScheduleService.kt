package com.example.vistaquickdonation.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class ScheduledDonationDto(
    val title: String = "",
    val dateMillis: Long = 0L,
    val timeText: String = "",
    val note: String? = null,
    val clothingType: String = "",
    val size: String = "",
    val brand: String = "",
    val userEmail: String = "",
    val createdAt: Timestamp? = null
)

class FirebaseScheduleService(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun col() = db.collection("scheduled_donations")

    suspend fun create(dto: ScheduledDonationDto): Boolean {
        val m = hashMapOf(
            "title" to dto.title,
            "dateMillis" to dto.dateMillis,
            "timeText" to dto.timeText,
            "note" to dto.note,
            "clothingType" to dto.clothingType,
            "size" to dto.size,
            "brand" to dto.brand,
            "userEmail" to dto.userEmail,
            "createdAt" to (dto.createdAt ?: Timestamp.now())
        )
        return try { col().add(m).await(); true } catch (_: Exception) { false }
    }
}
