package com.example.moviesocial.screens.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random



@Composable
fun FloatingHeart(
    heartData: HeartData,
    config: HeartConfig,
    onAnimationEnd: () -> Unit
) {
    val angle = remember { Random.nextDouble(-90.0, 180.0) }
    val baseRadius = remember { Random.nextDouble(100.0, 500.0).toFloat() }
    val radius = baseRadius * config.radiusMultiplier

    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(0.5f) }

    LaunchedEffect(key1 = heartData.id) {
        launch {
            xOffset.animateTo(
                targetValue = (radius * cos(Math.toRadians(angle))).toFloat(),
                animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
            )
        }

        // -250f -> going up
        launch {
            yOffset.animateTo(
                targetValue = (radius * -sin(Math.toRadians(angle))).toFloat() - 250f,
                animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
            )
        }


        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1400)
            )
            onAnimationEnd()
        }
    }


    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = null,
        tint = androidx.compose.ui.graphics.Color.Red.copy(alpha = alpha.value),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (heartData.startPosition.x + xOffset.value).roundToInt() - 16,
                    y = (heartData.startPosition.y + yOffset.value).roundToInt() - 16
                )
            }
            .size(32.dp)
    )
}

data class HeartData(
    val id: Int,
    val startPosition: Offset
)

data class HeartConfig(
    val radiusMultiplier: Float,
    val delayDuration: Long
)
