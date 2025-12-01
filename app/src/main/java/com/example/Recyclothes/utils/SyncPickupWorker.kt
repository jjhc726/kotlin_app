package com.example.Recyclothes.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.Recyclothes.data.repository.PickupRepository

class SyncPickupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repo = PickupRepository(context)

    override suspend fun doWork(): Result {
        return try {
            repo.syncPendingRequests()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
