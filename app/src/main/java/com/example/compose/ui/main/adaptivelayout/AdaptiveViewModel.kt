package com.example.compose.ui.main.adaptivelayout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AdaptiveViewModel: ViewModel() {
    private val _isFoldable = mutableStateOf(false)
    val isFoldable : State<Boolean> = _isFoldable

    fun setFoldable(value: Boolean){
        _isFoldable.value = value
    }
}