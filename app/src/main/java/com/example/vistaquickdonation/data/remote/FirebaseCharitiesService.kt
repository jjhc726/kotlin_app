package com.example.vistaquickdonation.data.remote

import com.example.vistaquickdonation.data.model.DonationPoint
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await



class FirebaseCharitiesService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val collectionName: String = "Charities"
) {
    private val charities = firestore.collection(collectionName)


    suspend fun getAllDonationPoints(): List<DonationPoint> {
        try {
            val snapshot = charities.get().await()
            return snapshot.documents.mapNotNull { docToDonationPoint(it.data, it.id) }
        } catch (e: Exception) {
            throw e
        }
    }


    private fun docToDonationPoint(data: Map<String, Any>?, documentId: String): DonationPoint? {
        if (data == null) return null

        val id = (data["id"] as? String) ?: documentId
        val name = data["name"] as? String ?: return null
        val cause = data["cause"] as? String ?: ""
        val accessible = when (val v = data["accessible"]) {
            is Boolean -> v
            is String -> v.toBoolean()
            is Number -> v.toInt() != 0
            else -> false
        }
        val schedule = data["schedule"] as? String ?: ""


        val position: LatLng? = (data["position"] as? GeoPoint)?.let { geo ->
            LatLng(geo.latitude, geo.longitude)
        }


        val finalPosition = position ?: return null // si necesita position obligatoria

        return DonationPoint(
            id = id,
            name = name,
            position = finalPosition,
            cause = cause,
            accessible = accessible,
            schedule = schedule
        )
    }
}
