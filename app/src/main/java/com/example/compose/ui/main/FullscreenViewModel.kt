package com.example.compose.ui.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FullscreenViewModel : ViewModel() {
    // UI 상태를 선언 (Private 수정, Public 읽기 전용)
    private val _isFullscreen = mutableStateOf(false)
    val isFullscreen: State<Boolean> = _isFullscreen

    private val _isControlsVisible = mutableStateOf(true)
    val isControlsVisible: State<Boolean> = _isControlsVisible

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
        _isControlsVisible.value = !_isFullscreen.value
    }

    fun setFullscreen(value: Boolean) {
        _isFullscreen.value = value
        _isControlsVisible.value = !value
    }
}