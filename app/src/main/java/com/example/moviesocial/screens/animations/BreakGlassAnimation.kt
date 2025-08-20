package com.example.moviesocial.screens.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun BreakingCrack(
    lineData: LineData,
    config: LineConfig,
) {
    val angle = remember { Random.nextDouble(0.0, 360.0) }
    val length = remember { Random.nextDouble(config.minLength.toDouble(), config.maxRadius.toDouble()).toFloat() }
    val thickness = remember { Random.nextDouble(1.0, config.maxThickness.toDouble()).toFloat() }

    val progress = remember { Animatable(0f) }
    val alpha = 1f

    LaunchedEffect(key1 = lineData.id) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
            )
        }
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val currentLength = length * progress.value
        val crackPath = generateCrackPath(lineData.startPosition, angle, currentLength)

        if (crackPath.size >= 2) {
            // first segment
            drawLine(
                color = Color(0xFFADD8E6).copy(alpha = alpha),
                start = crackPath[0],
                end = crackPath[1],
                strokeWidth = thickness * (1..2).random(),
                cap = StrokeCap.Round,
                alpha = 0.9f
            )

            //  second segment
            if (crackPath.size >= 3) {
                drawLine(
                    color = Color(0xFF87CEFA).copy(alpha = alpha) , // cold blue-gray ,
                    start = crackPath[1],
                    end = crackPath[2],
                    strokeWidth = thickness * (1..3).random(),
                    cap = StrokeCap.Round,
                    alpha = 0.5f
                )
            }
        }
    }
}

private fun generateCrackPath(startPosition: Offset, angle: Double, totalLength: Float): List<Offset> {
    val points = mutableListOf<Offset>()
    points.add(startPosition)

    val firstSegmentRatio = 0.4f + Random.nextFloat() * 0.3f
    val firstSegmentLength = totalLength * firstSegmentRatio

    // first break point
    val breakPointX = startPosition.x + (firstSegmentLength * cos(angle)).toFloat()
    val breakPointY = startPosition.y + (firstSegmentLength * sin(angle)).toFloat()
    points.add(Offset(breakPointX, breakPointY))

    // second break
    val angleVariation = (Random.nextFloat() - 0.5f) * 0.4f
    val newAngle = angle + angleVariation
    val remainingLength = totalLength - firstSegmentLength

    // end point
    val endX = breakPointX + (remainingLength * cos(newAngle)).toFloat()
    val endY = breakPointY + (remainingLength * sin(newAngle)).toFloat()
    points.add(Offset(endX, endY))

    return points
}

data class LineData(
    val id: Int,
    val startPosition: Offset
)

data class LineConfig(
    val radiusMultiplier: Float,
    val delayDuration: Long,
    val maxRadius: Float,
    val minLength: Float,
    val maxThickness: Float
)