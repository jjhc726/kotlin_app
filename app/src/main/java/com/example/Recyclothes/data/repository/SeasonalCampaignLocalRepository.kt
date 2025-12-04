package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.local.dao.SeasonalCampaignDao
import com.example.Recyclothes.data.local.entity.SeasonalCampaignEntity

class SeasonalCampaignLocalRepository(
    private val dao: SeasonalCampaignDao
) {

    fun getCampaigns() = dao.getCampaigns()

    suspend fun saveCampaigns(campaigns: List<SeasonalCampaignEntity>) {
        dao.insertCampaigns(campaigns)
    }
}
