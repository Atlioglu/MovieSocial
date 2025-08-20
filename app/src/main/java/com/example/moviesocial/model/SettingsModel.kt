package com.example.moviesocial.model

data class SettingsModel(
    val isAnimationEnabled: Boolean = false,
    val selectedUIIndex: Int = 0,
    val uiOptions: List<String> = listOf("Grid List", "Row List")
)