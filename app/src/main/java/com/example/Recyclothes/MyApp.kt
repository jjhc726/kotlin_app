package com.example.Recyclothes

import android.app.Application
import com.example.Recyclothes.data.repository.PickupRepository
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApp : Application() {

    private lateinit var networkObserver: NetworkObserver
    private lateinit var pickupRepo: PickupRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()


        networkObserver = NetworkObserver(this)
        pickupRepo = PickupRepository(this)

        networkObserver.registerCallback(
            onAvailable = {
                GlobalScope.launch(Dispatchers.IO) {
                    pickupRepo.syncPendingRequests()
                }
            },
            onLost = { }
        )
    }
}
