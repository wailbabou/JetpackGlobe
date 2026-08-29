package com.ouail.globe

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ouail.globe.math.GlobeMath
import com.ouail.globe.model.GlobePoint
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun <T : GlobePoint> GlobeView(
    items: List<T>,
    modifier: Modifier = Modifier,
    selectedItem: T? = null,
    onItemSelected: (T?) -> Unit = {},
    config: GlobeConfig = GlobeDefaults.DefaultConfig,
    showWireframe: Boolean = true,
    wireframeColor: Color = Color.White.copy(alpha = 0.15f),
    centerGlowColor: Color = Color.White.copy(alpha = 0.06f),
    itemContent: @Composable (item: T, isSelected: Boolean, normalizedZ: Float) -> Unit
) {
    if (items.isEmpty()) return

    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (selectedItem != null) {
                    onItemSelected(null)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val diameterDp = maxWidth
        val radiusPx = with(density) { (diameterDp.toPx() - config.paddingDp.toPx() * 2) / 2f }

        val unitPoints = remember(items.size) {
            GlobeMath.calculateFibonacciPoints(items.size)
        }

        val scope = rememberCoroutineScope()
        val rotXAnimatable = remember { Animatable(0f) }
        val rotYAnimatable = remember { Animatable(0f) }

        // Rotation & Centering logic
        LaunchedEffect(selectedItem, config.isAutoRotationEnabled, config.autoRotationDurationMs) {
            if (selectedItem == null) {
                if (config.isAutoRotationEnabled && config.autoRotationDurationMs > 0) {
                    val speed = 360f / config.autoRotationDurationMs
                    var lastTime = withFrameNanos { it }
                    while (true) {
                        val delta = withFrameNanos { now ->
                            val deltaMs = (now - lastTime) / 1_000_000f
                            lastTime = now
                            deltaMs * speed
                        }
                        rotYAnimatable.snapTo(rotYAnimatable.value + delta)
                    }
                }
            } else {
                val index = items.indexOf(selectedItem)
                if (index in unitPoints.indices) {
                    val target = GlobeMath.calculateCenterAngles(
                        point = unitPoints[index],
                        currentRotX = rotXAnimatable.value,
                        currentRotY = rotYAnimatable.value
                    )
                    launch {
                        rotXAnimatable.animateTo(
                            targetValue = target.first,
                            animationSpec = tween(
                                durationMillis = config.centeringDurationMs,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                    launch {
                        rotYAnimatable.animateTo(
                            targetValue = target.second,
                            animationSpec = tween(
                                durationMillis = config.centeringDurationMs,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            }
        }

        // Gesture handling Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedItem) {
                    if (selectedItem == null) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                rotYAnimatable.snapTo(
                                    rotYAnimatable.value + (dragAmount.x * config.dragSensitivity)
                                )
                                rotXAnimatable.snapTo(
                                    rotXAnimatable.value - (dragAmount.y * config.dragSensitivity)
                                )
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Optional Aesthetic 3D Ambient Wireframe & Glow
            if (showWireframe) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pulse = if (selectedItem != null) 0.6f else 1f
                            alpha = pulse
                        }
                ) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // Core glowing gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                centerGlowColor,
                                Color.Transparent
                            ),
                            center = centerOffset,
                            radius = radiusPx * 1.15f
                        ),
                        radius = radiusPx * 1.15f,
                        center = centerOffset
                    )

                    // Equator and Meridian rings for 3D sphere feel
                    drawCircle(
                        color = wireframeColor,
                        radius = radiusPx,
                        center = centerOffset,
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
            }

            // Projected 3D nodes
            items.forEachIndexed { index, item ->
                if (index < unitPoints.size) {
                    val (x0, y0, z0) = unitPoints[index]
                    val projected = GlobeMath.projectPoint(
                        x0 = x0,
                        y0 = y0,
                        z0 = z0,
                        rotXDeg = rotXAnimatable.value,
                        rotYDeg = rotYAnimatable.value,
                        radiusPx = radiusPx
                    )

                    val isSelected = selectedItem == item
                    val zoomScale by animateFloatAsState(
                        targetValue = if (isSelected) config.selectedZoomScale else 1.0f,
                        animationSpec = tween(400),
                        label = "GlobeZoomScale"
                    )

                    val blurRadius by animateDpAsState(
                        targetValue = if (selectedItem != null && !isSelected) config.unselectedBlurRadius else 0.dp,
                        animationSpec = tween(400),
                        label = "GlobeBlurRadius"
                    )

                    val depthAlpha = if (isSelected) {
                        1f
                    } else {
                        (projected.normalizedZ * 1.25f).coerceIn(config.minDepthAlpha, 1f)
                    }

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = projected.screenX
                                translationY = projected.screenY
                                scaleX = projected.scale * zoomScale
                                scaleY = projected.scale * zoomScale
                                alpha = depthAlpha
                            }
                            .then(
                                if (blurRadius > 0.dp) Modifier.blur(blurRadius) else Modifier
                            )
                            .zIndex(if (isSelected) 1000f else projected.screenZ)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onItemSelected(if (isSelected) null else item)
                            }
                    ) {
                        itemContent(item, isSelected, projected.normalizedZ)
                    }
                }
            }
        }
    }
}
