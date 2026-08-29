package com.ouail.jetpackglobe.model

import com.ouail.globe.model.GlobePoint

data class SampleUser(
    override val id: String,
    val name: String,
    val role: String,
    val city: String,
    val countryFlag: String,
    val avatarUrl: String,
    val rating: Float = 4.9f,
    val reactions: List<String> = listOf("🔥", "⚡️", "✨", "❤️")
) : GlobePoint
