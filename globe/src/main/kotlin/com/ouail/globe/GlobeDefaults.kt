package com.ouail.globe

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration options for GlobeView styling, animation speeds, and interaction bounds.
 */
data class GlobeConfig(
    val autoRotationDurationMs: Int = 22000,
    val isAutoRotationEnabled: Boolean = true,
    val selectedZoomScale: Float = 1.45f,
    val unselectedBlurRadius: Dp = 6.dp,
    val minDepthAlpha: Float = 0.15f,
    val dragSensitivity: Float = 0.22f,
    val centeringDurationMs: Int = 750,
    val paddingDp: Dp = 48.dp
)

object GlobeDefaults {
    val DefaultConfig = GlobeConfig()
}
