package com.ouail.globe.model

/**
 * Interface representing an identifiable item placed on the 3D globe.
 */
interface GlobePoint {
    val id: String
}

/**
 * Default basic data class implementation for GlobePoint.
 */
data class DefaultGlobeItem(
    override val id: String,
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String? = null
) : GlobePoint
