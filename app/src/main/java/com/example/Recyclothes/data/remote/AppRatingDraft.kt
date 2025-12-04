package com.example.Recyclothes.data.remote

data class AppRatingDraft(
    val stars: Int = 0,
    val likeMost: String = "",
    val improvements: List<String> = emptyList(),
    val recommend: Boolean? = null,
    val comments: String = ""
)
