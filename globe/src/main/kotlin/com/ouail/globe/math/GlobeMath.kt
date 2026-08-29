package com.ouail.globe.math

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Float.toRadians(): Float = (this * (PI / 180.0)).toFloat()
fun Float.toDegrees(): Float = (this * (180.0 / PI)).toFloat()
fun Double.toDegrees(): Double = (this * (180.0 / PI))

data class ProjectedPoint(
    val screenX: Float,
    val screenY: Float,
    val screenZ: Float,
    val normalizedZ: Float,
    val scale: Float
)

object GlobeMath {

    /**
     * Generate uniformly distributed 3D points on a sphere using the Fibonacci lattice algorithm.
     */
    fun calculateFibonacciPoints(count: Int): List<Triple<Float, Float, Float>> {
        if (count <= 0) return emptyList()
        val goldenRatio = (1.0 + sqrt(5.0))
        return List(count) { i ->
            val phi = acos(1.0 - 2.0 * (i + 0.5) / count)
            val theta = PI * goldenRatio * i
            Triple(
                (sin(phi) * cos(theta)).toFloat(),
                cos(phi).toFloat(),
                (sin(phi) * sin(theta)).toFloat()
            )
        }
    }

    /**
     * Project a 3D unit sphere point to 2D screen coordinates based on pitch (rotX) and yaw (rotY).
     */
    fun projectPoint(
        x0: Float,
        y0: Float,
        z0: Float,
        rotXDeg: Float,
        rotYDeg: Float,
        radiusPx: Float
    ): ProjectedPoint {
        val radX = rotXDeg.toRadians()
        val radY = rotYDeg.toRadians()

        // Pitch rotation (around X-axis)
        val y1 = y0 * cos(radX) - z0 * sin(radX)
        val z1 = y0 * sin(radX) + z0 * cos(radX)
        val x1 = x0

        // Yaw rotation (around Y-axis)
        val x2 = x1 * cos(radY) + z1 * sin(radY)
        val z2 = -x1 * sin(radY) + z1 * cos(radY)
        val y2 = y1

        val screenX = x2 * radiusPx
        val screenY = y2 * radiusPx
        val screenZ = z2 * radiusPx

        // normalizedZ from 0 (farthest) to 1 (closest)
        val normalizedZ = ((screenZ / radiusPx) + 1f) / 2f
        val scale = 0.55f + 0.45f * normalizedZ

        return ProjectedPoint(
            screenX = screenX,
            screenY = screenY,
            screenZ = screenZ,
            normalizedZ = normalizedZ.coerceIn(0f, 1f),
            scale = scale
        )
    }

    /**
     * Finds the closest angle representation to `current` so rotation travels the shortest distance.
     */
    fun normalizeAngle(current: Float, target: Float): Float {
        var diff = (target - current) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return current + diff
    }

    /**
     * Calculates the target pitch and yaw rotation angles required to center a 3D point on the screen.
     */
    fun calculateCenterAngles(
        point: Triple<Float, Float, Float>,
        currentRotX: Float,
        currentRotY: Float
    ): Pair<Float, Float> {
        val (x0, y0, z0) = point

        val rawTargetX = atan2(y0.toDouble(), z0.toDouble()).toDegrees().toFloat()
        val targetRotX = normalizeAngle(currentRotX, rawTargetX)

        val radX = targetRotX.toRadians()
        val z1 = y0 * sin(radX) + z0 * cos(radX)
        val rawTargetY = atan2(-x0.toDouble(), z1.toDouble()).toDegrees().toFloat()
        val targetRotY = normalizeAngle(currentRotY, rawTargetY)

        return Pair(targetRotX, targetRotY)
    }
}
