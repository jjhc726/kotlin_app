package com.example.Recyclothes

import android.app.Application
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.Recyclothes.data.repository.PickupRepository
import com.example.Recyclothes.utils.NetworkObserver
import com.example.Recyclothes.utils.SyncPickupWorker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val networkObserver = NetworkObserver(this)

        networkObserver.registerCallback(
            onAvailable = {
                enqueuePickupSyncWork()
            },
            onLost = {}
        )
    }

    private fun enqueuePickupSyncWork() {
        val request = OneTimeWorkRequestBuilder<SyncPickupWorker>().build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "pickup_sync_work",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}



