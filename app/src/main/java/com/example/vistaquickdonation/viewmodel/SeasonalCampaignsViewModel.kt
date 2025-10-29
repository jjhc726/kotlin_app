package com.example.vistaquickdonation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SeasonalCampaign(
    val id: Int,
    val title: String,
    val description: String
)

class SeasonalCampaignsViewModel : ViewModel() {

    private val _campaigns = MutableStateFlow(
        listOf(
            SeasonalCampaign(1, "Winter Warmth Drive", "Help families in need with coats and blankets."),
            SeasonalCampaign(2, "Back to School", "Support children with school supplies."),
            SeasonalCampaign(3, "Holiday Meals", "Provide meals for families during the holidays.")
        )
    )
    val campaigns: StateFlow<List<SeasonalCampaign>> = _campaigns
}
