package com.example.moviesocial.screens.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import android.content.Context
import android.widget.Toast
import com.example.moviesocial.model.SettingsModel

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsModel())
    val settingsState: State<SettingsModel> = _settingsState

    private val _dropdownExpanded = mutableStateOf(false)
    val dropdownExpanded: State<Boolean> = _dropdownExpanded

    private val UI_PREFS = "UI_PREFS"
    private val ANIMATION_PREFS = "ANIMATION_PREFS"
    private val SELECTED_UI_INDEX_KEY = "selected_ui_index"
    private val ANIMATION_STATUS_KEY = "animation_statue"

    fun loadSettings() {
        val uiSharedPref = context.getSharedPreferences(UI_PREFS, Context.MODE_PRIVATE)
        val animationSharedPref = context.getSharedPreferences(ANIMATION_PREFS, Context.MODE_PRIVATE)

        val selectedIndex = uiSharedPref.getInt(SELECTED_UI_INDEX_KEY, 0)
        val isAnimationEnabled = animationSharedPref.getBoolean(ANIMATION_STATUS_KEY, false)

        _settingsState.value = _settingsState.value.copy(
            selectedUIIndex = selectedIndex,
            isAnimationEnabled = isAnimationEnabled
        )
    }

    fun updateUISelection(index: Int) {
        _settingsState.value = _settingsState.value.copy(selectedUIIndex = index)
        setDropdownExpanded(false)
    }

    fun updateAnimationSetting(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(isAnimationEnabled = enabled)
    }

    fun setDropdownExpanded(expanded: Boolean) {
        _dropdownExpanded.value = expanded
    }

    fun saveSettings() {
        val uiSharedPref = context.getSharedPreferences(UI_PREFS, Context.MODE_PRIVATE)
        val animationSharedPref = context.getSharedPreferences(ANIMATION_PREFS, Context.MODE_PRIVATE)

        uiSharedPref.edit().putInt(SELECTED_UI_INDEX_KEY, _settingsState.value.selectedUIIndex).apply()
        animationSharedPref.edit().putBoolean(ANIMATION_STATUS_KEY, _settingsState.value.isAnimationEnabled).apply()

        Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
    }
}