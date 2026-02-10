package com.example.compose.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnimatedOrderedListViewModel : ViewModel() {
    private val _data = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "11", "12", "13", "14", "15", "16")
    private val _displayedItems: MutableStateFlow<List<String>> = MutableStateFlow(_data)

    // collectAsStateWithLifecycle() 호출로 Flow<T> 에서 방출되는 최신 값을 받아 State<T> 타입으로 변환
    // 이렇게 변환된 State<T> 값을 Composable 내에서 읽으면 Flow 에서 새로운 값이 방출될 때마다 Compose Recomposition 이 자동으로 발생하여 UI 가 최신 데이터로 업데이트됨
    val displayedItems: StateFlow<List<String>> = _displayedItems

    // 기존 collectAsSate() 와 차이점
    // collectAsSate() / collectAsStateWithLifecycle()
    // 라이프사이클 인식(x) / 라이프사이클 인식(o)
    // 컴포저블이 컴포지션에 있을 때만 항상 수집 / 컴포저블이 지정된 LifeCycle State 에 있을 때만 수집(기본값 : LifeCycle.State.STARTED)
    // 간단하지만 비활성 시에도 불필요한 리소스 소모 가능성 / 리소스 절약 및 안전성 보장

    fun resetOrder(){
        _displayedItems.value = _data.filter { it in _displayedItems.value }
    }

    fun sortAlphabetically(){
        _displayedItems.value = _displayedItems.value.sortedBy { it }
    }

    fun sortByLength(){
        _displayedItems.value = _displayedItems.value.sortedBy { it.length }
    }

    fun addItem(){
        // Avoid duplicate items
        val remainingItems = _data.filter { it !in _displayedItems.value }
        Log.d("remainingItems", "${remainingItems}")

        // 남은 아이템이 존재할 때(isNotEmpty)만 실행
        if (remainingItems.isNotEmpty()) {
            _displayedItems.value += remainingItems.first()
        }
    }

    fun removeItem(){
        _displayedItems.value = _displayedItems.value.dropLast(1)
    }


}