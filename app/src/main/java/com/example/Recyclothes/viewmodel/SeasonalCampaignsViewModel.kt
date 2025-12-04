package com.example.Recyclothes.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.util.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.R
import androidx.room.Room
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.entity.SeasonalCampaignEntity
import com.example.Recyclothes.data.model.SeasonalCampaign
import com.example.Recyclothes.data.repository.InteractionRepository
import com.example.Recyclothes.data.repository.SeasonalCampaignLocalRepository
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class SeasonalCampaignsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "recyclothes_db"
    ).build()

    private val repository = SeasonalCampaignLocalRepository(db.seasonalCampaignDao())

    private val campaignCache = LruCache<Int, SeasonalCampaign>(20)

    private val _campaigns = MutableStateFlow<List<SeasonalCampaign>>(emptyList())
    val campaigns: StateFlow<List<SeasonalCampaign>> = _campaigns

    private val interactionRepository = InteractionRepository()

    private val _selectedCampaign = MutableStateFlow<SeasonalCampaign?>(null)
    val selectedCampaign: StateFlow<SeasonalCampaign?> = _selectedCampaign

    private val network = NetworkObserver(application)
    private val _networkStatus = MutableStateFlow(network.isOnline())
    val networkStatus: StateFlow<Boolean> = _networkStatus

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {

            repository.getCampaigns().collect { entities ->

                if (entities.isNotEmpty()) {

                    val list = entities.map {
                        SeasonalCampaign(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            date = it.date,
                            location = it.location,
                            imageRes = it.imageRes
                        )
                    }

                    _campaigns.value = list

                    list.forEach { campaign ->
                        campaignCache.put(campaign.id, campaign)
                    }

                } else {
                    val generated = generateSeasonalCampaigns()
                    repository.saveCampaigns(generated)
                }
            }
        }
    }

    private fun generateSeasonalCampaigns(): List<SeasonalCampaignEntity> {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1

        val winterImg = R.drawable.camp_winter
        val holidayImg = R.drawable.camp_holiday
        val springImg = R.drawable.camp_spring
        val earthImg = R.drawable.camp_earth
        val summerImg = R.drawable.camp_summer
        val schoolImg = R.drawable.camp_school
        val autumnImg = R.drawable.camp_autumn
        val thanksImg = R.drawable.camp_thanks

        return when (month) {
            12, 1, 2 -> listOf(
                SeasonalCampaignEntity(
                    1, "Winter Warmth Drive",
                    "Donate warm clothes and blankets to help those in need during the cold season.",
                    "Del 5 al 20 de enero de 2025", "Centro Comunitario de Bogotá", winterImg
                ),
                SeasonalCampaignEntity(
                    2, "Holiday Donation Week",
                    "Support families with toys, clothes, and food this holiday season.",
                    "Del 10 al 24 de diciembre de 2024", "Plaza Central de Medellín", holidayImg
                )
            )

            3, 4, 5 -> listOf(
                SeasonalCampaignEntity(
                    3, "Spring Renewal",
                    "Give your gently used clothes a new life and support local shelters.",
                    "Del 15 al 2 de mayo de 2025", "Parque de los Deseos, Medellín", springImg
                ),
                SeasonalCampaignEntity(
                    4, "Earth Month Campaign",
                    "Join us in promoting sustainable fashion by donating recyclable garments.",
                    "Durante todo abril de 2025", "EcoCentro, Cali", earthImg
                )
            )

            6, 7, 8 -> listOf(
                SeasonalCampaignEntity(
                    5, "Summer Relief",
                    "Help provide light clothes and essentials to those in warm conditions.",
                    "Del 1 al 15 de julio de 2025", "Parque Simón Bolívar, Bogotá", summerImg
                ),
                SeasonalCampaignEntity(
                    6,
                    "Back to School Drive",
                    "Donate school uniforms and supplies for kids starting the new term.",
                    "Del 10 al 30 de agosto de 2025",
                    "Fundación Educando Sonrisas, Bucaramanga",
                    schoolImg
                )
            )

            9, 10, 11 -> listOf(
                SeasonalCampaignEntity(
                    7,
                    "Autumn Donation Fair",
                    "Share coats and warm wear as temperatures begin to drop.",
                    "Del 20 de septiembre al 5 de octubre de 2025",
                    "Plaza Bolívar, Bogotá",
                    autumnImg
                ),
                SeasonalCampaignEntity(
                    8, "Thanksgiving Support",
                    "Contribute clothing and essentials to families in preparation for the holidays.",
                    "Del 1 al 15 de noviembre de 2025", "Centro Cultural de Cartagena", thanksImg
                )
            )

            else -> emptyList()
        }
    }

    fun selectCampaign(campaign: SeasonalCampaign) {

        val cached = campaignCache.get(campaign.id)
        if (cached != null) {
            _selectedCampaign.value = cached
            return
        }

        campaignCache.put(campaign.id, campaign)
        _selectedCampaign.value = campaign
    }

    fun goBack() {
        _selectedCampaign.value = null
    }

    fun addInteraction() {
        viewModelScope.launch { interactionRepository.addCampaignInteraction() }
    }

    override fun onCleared() {
        super.onCleared()
        networkCallback?.let { network.unregisterCallback(it) }
    }

    fun startNetworkObserver() {
        if (networkCallback != null) return

        networkCallback = network.registerCallback(
            onAvailable = { _networkStatus.value = true },
            onLost = { _networkStatus.value = false }
        )
    }
}
