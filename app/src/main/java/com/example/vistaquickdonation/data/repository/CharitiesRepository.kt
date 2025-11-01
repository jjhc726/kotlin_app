package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.local.AppDatabase
import com.example.vistaquickdonation.data.local.toEntity
import com.example.vistaquickdonation.data.model.DonationPoint
import com.example.vistaquickdonation.data.remote.FirebaseCharitiesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context


class CharitiesRepository(
    private val context: Context,
    private val service: FirebaseCharitiesService = FirebaseCharitiesService()
) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.donationPointDao()

    suspend fun getAllDonationPoints(): List<DonationPoint> {
        return try {
            // Llamada al servicio remoto (suponiendo que service.getAllDonationPoints() es suspend)
            val remotePoints = withContext(Dispatchers.IO) {
                service.getAllDonationPoints()
            }

            // Guardar en Room (mapear dominio -> entidad)
            withContext(Dispatchers.IO) {
                dao.insertAll(remotePoints.map { it.toEntity() })
            }

            remotePoints
        } catch (e: Exception) {
            // Fallback: devolver datos locales si hay error remoto
            e.printStackTrace()
            withContext(Dispatchers.IO) {
                dao.getAll().map { it.toDomain() }
            }
        }
    }

        suspend fun getLocalDonationPoints(): List<DonationPoint> {
            return withContext(Dispatchers.IO) {
                dao.getAll().map { it.toDomain() }
            }
        }

        suspend fun refreshRemoteAndCache(): List<DonationPoint> {
            val remote = withContext(Dispatchers.IO) {
                service.getAllDonationPoints()
            }
            withContext(Dispatchers.IO) {
                dao.insertAll(remote.map { it.toEntity() })
            }
            return remote
        }
    }

