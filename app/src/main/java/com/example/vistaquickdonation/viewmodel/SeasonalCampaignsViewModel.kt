package com.example.vistaquickdonation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.model.SeasonalCampaign
import com.example.vistaquickdonation.data.repository.InteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeasonalCampaignsViewModel : ViewModel() {

    private val _campaigns = MutableStateFlow<List<SeasonalCampaign>>(emptyList())
    val campaigns: StateFlow<List<SeasonalCampaign>> = _campaigns

    private val _totalInteractions = MutableStateFlow<Long>(0)

    private val interactionRepository = InteractionRepository()

    init {
        loadCampaigns()
        loadTotalInteractions()
    }

    private fun loadCampaigns() {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

        val campaigns = when (month) {
            12, 1, 2 -> listOf(
                SeasonalCampaign(
                    id = 1,
                    title = "Winter Warmth Drive",
                    description = "Donate warm clothes and blankets to help those in need during the cold season.",
                    date = "Del 5 al 20 de enero de 2025",
                    location = "Centro Comunitario de Bogotá"
                ),
                SeasonalCampaign(
                    id = 2,
                    title = "Holiday Donation Week",
                    description = "Support families with toys, clothes, and food this holiday season.",
                    date = "Del 10 al 24 de diciembre de 2024",
                    location = "Plaza Central de Medellín"
                )
            )
            3, 4, 5 -> listOf(
                SeasonalCampaign(
                    id = 3,
                    title = "Spring Renewal",
                    description = "Give your gently used clothes a new life and support local shelters.",
                    date = "Del 15 de abril al 2 de mayo de 2025",
                    location = "Parque de los Deseos, Medellín"
                ),
                SeasonalCampaign(
                    id = 4,
                    title = "Earth Month Campaign",
                    description = "Join us in promoting sustainable fashion by donating recyclable garments.",
                    date = "Durante todo abril de 2025",
                    location = "EcoCentro, Cali"
                )
            )
            6, 7, 8 -> listOf(
                SeasonalCampaign(
                    id = 5,
                    title = "Summer Relief",
                    description = "Help provide light clothes and essentials to those in warm conditions.",
                    date = "Del 1 al 15 de julio de 2025",
                    location = "Parque Simón Bolívar, Bogotá"
                ),
                SeasonalCampaign(
                    id = 6,
                    title = "Back to School Drive",
                    description = "Donate school uniforms and supplies for kids starting the new term.",
                    date = "Del 10 al 30 de agosto de 2025",
                    location = "Fundación Educando Sonrisas, Bucaramanga"
                )
            )
            9, 10, 11 -> listOf(
                SeasonalCampaign(
                    id = 7,
                    title = "Autumn Donation Fair",
                    description = "Share coats and warm wear as temperatures begin to drop.",
                    date = "Del 20 de septiembre al 5 de octubre de 2025",
                    location = "Plaza Bolívar, Bogotá"
                ),
                SeasonalCampaign(
                    id = 8,
                    title = "Thanksgiving Support",
                    description = "Contribute clothing and essentials to families in preparation for the holidays.",
                    date = "Del 1 al 15 de noviembre de 2025",
                    location = "Centro Cultural de Cartagena"
                )
            )
            else -> emptyList()
        }

        _campaigns.value = campaigns
    }

    fun getCampaignById(id: Int): SeasonalCampaign? {
        return _campaigns.value.find { it.id == id }
    }

    fun addInteraction() {
        viewModelScope.launch {
            interactionRepository.addInteraction()
            loadTotalInteractions()
        }
    }
    private fun loadTotalInteractions() {
        viewModelScope.launch {
            _totalInteractions.value = interactionRepository.fetchTotalInteractions()
        }
    }
}
