package com.example.moviesocial.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.pow


data class Line(
    val start: Offset,
    val end: Offset,
    val strokeWidth: Float
){
    fun calculateArea(): Float {
        val lineLength = calculateLength()
        return lineLength * strokeWidth
    }
    private fun calculateLength(): Float {
        return kotlin.math.sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
    }
}