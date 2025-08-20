package com.example.moviesocial.screens.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.roundToInt

private val BULB_RADIUS = 18.dp
private val SOCKET_HEIGHT = 10.dp
private val SOCKET_WIDTH = 10.dp
private val CABLE_WIDTH = 2.dp
private val BULB_START_X = 0.5f
private val BULB_START_Y = 0.1f
// so the starting point is the center horizontally and ten percents from top vertically



//drawing the cable through the light bulb.
fun Modifier.bulbAttachments(
    state: LightingState
): Modifier {
    return drawBehind {
        val bulbPosition = state.bulbOffset

        drawLine(
            Color.DarkGray,
            start = center.copy(y = 0f),
            end = bulbPosition,
            CABLE_WIDTH.toPx()
        )

        val angle = atan((bulbPosition.x - center.x) / (bulbPosition.y))

        val rectHeight = SOCKET_HEIGHT.toPx()
        val rectWidth = SOCKET_WIDTH.toPx()

        withTransform({
            rotate(-(angle / PI * 180).toFloat(), pivot = center.copy(y = 0f))
        }) {
            drawRect(
                Color.LightGray,
                topLeft = Offset(
                    x = center.x - rectWidth / 2,
                    y = (bulbPosition - center.copy(y = 0f)).getDistance() - (BULB_RADIUS.toPx() + rectHeight)
                ),
                size = Size(rectWidth, rectHeight),
            )
        }
    }
}


@Composable
fun LightBulb(state: LightingState) {
    val coroutineScope = rememberCoroutineScope()

    Box(Modifier
        .size(36.dp)
        .offset {
            val position = state.bulbOffset - Offset(BULB_RADIUS.toPx(), BULB_RADIUS.toPx())
            IntOffset(position.x.roundToInt(), position.y.roundToInt())
        }
        .background(
            state.bulbBackground,
            shape = CircleShape
        )
        .shadow(16.dp, shape = CircleShape, clip = false)
        .pointerInput(Unit) {
            detectTapGestures {
                coroutineScope.launch {
                    state.handleBulbClick()
                }
            }
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = { coroutineScope.launch { state.onDragEnd() } },
                onDragCancel = { coroutineScope.launch { state.onDragEnd() } },
                onDrag = { change, dragAmount ->
                    coroutineScope.launch { state.onDrag(change, dragAmount) }
                }
            )
        }
    )
}



class LightingState(private val size: Size, val scrollState: ScrollState) {
    var isLightOn by mutableStateOf(false)
        private set

    var isBulbBroken by mutableStateOf(false)
        private set

    private var clickTimes = mutableListOf<Long>()
    private val clickTimeWindow = 3000L
    private val maxClicksToBreak = 5


    // check if the bulb is broken or not, control breaking the bulb and toogle the light (turn on or off)
    suspend fun handleBulbClick() {
        if (isBulbBroken) return
        // if its nroken don't do anything

        val currentTime = System.currentTimeMillis()

        clickTimes.add(currentTime)

        clickTimes.removeAll { currentTime - it > clickTimeWindow }

        if (clickTimes.size >= maxClicksToBreak) {
            // Break the bulb
            isBulbBroken = true
            isLightOn = false
            clickTimes.clear()
        } else {
            toggleLight()
        }
    }

    private fun toggleLight() {
        if (!isBulbBroken) {
            isLightOn = !isLightOn
        }
    }

    val dragAnimatable = Animatable(Offset(0f, 0f), Offset.VectorConverter)

    private val scrollOffset by derivedStateOf {
        Offset(0f, scrollState.value.toFloat())
    }

    val bulbOffset by derivedStateOf {
        dragAnimatable.value.exactPositionIn()
    }

    // when the holding ends, set the bulb the first location (thanks to spring its smooth)
    suspend fun onDragEnd() {
        dragAnimatable.animateTo(
            Offset.Zero,
            spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)
        )
    }

    suspend fun onDrag(change: PointerInputChange, dragAmount: Offset) {
        change.consume()
        dragAnimatable.snapTo(dragAnimatable.value + dragAmount)
    }

    // colors with brush
    val bulbBackground by derivedStateOf {
        when {
            isBulbBroken -> {
                Brush.radialGradient(listOf(
                    Color(0xFF3A3A3A),
                    Color(0xFF1A1A1A),
                    Color.Black
                ))
            }
            isLightOn -> {
                Brush.radialGradient(listOf(Color.Yellow, Color(0xff85733a)))
            }
            else -> {
                SolidColor(Color.Gray)
            }
        }
    }

    // when the light on, show the lightbulb in a correct place and correct color
    val lightSourceBrush by derivedStateOf {
        when {
            isBulbBroken -> {
                SolidColor(Color.DarkGray)
            }
            isLightOn -> {
                Brush.radialGradient(
                    center = bulbOffset - scrollOffset,
                    radius = size.minDimension,
                    colors = listOf(Color.Yellow, Color(0xff85733a), Color.DarkGray)
                )
            }
            else -> {
                SolidColor(Color.DarkGray)
            }
        }
    }

    val buttonBackgroundBrush by derivedStateOf {
        when {
            isBulbBroken -> {
                SolidColor(Color.DarkGray.copy(alpha = 0.8f))
            }
            isLightOn -> {
                Brush.radialGradient(
                    center = bulbOffset + scrollOffset,
                    radius = size.minDimension * 0.3f,
                    colors = listOf(
                        Color.Yellow.copy(alpha = 0.3f),
                        Color(0xff85733a).copy(alpha = 0.4f),
                        Color.DarkGray.copy(alpha = 0.6f)
                    )
                )
            }
            else -> {
                SolidColor(Color.DarkGray.copy(alpha = 0.6f))
            }
        }
    }

    val cardBackgroundBrush by derivedStateOf {
        when {
            isBulbBroken -> {
                SolidColor(Color.Black.copy(alpha = 0.95f))
            }
            isLightOn -> {
                Brush.radialGradient(
                    center = bulbOffset + scrollOffset,
                    radius = size.minDimension * 0.8f,
                    colors = listOf(
                        Color.Yellow.copy(alpha = 0.1f),
                        Color(0xff85733a).copy(alpha = 0.2f),
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            }
            else -> {
                SolidColor(Color.Black.copy(alpha = 0.9f))
            }
        }
    }

    private fun Offset.exactPositionIn(): Offset {
        return this + Offset(size.width * BULB_START_X, size.height * BULB_START_Y)
    }
}