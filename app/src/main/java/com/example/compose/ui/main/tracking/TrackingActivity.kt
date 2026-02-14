package com.example.compose.ui.main.tracking

import android.R.attr.background
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme

/**
 * Compose 노출 추적 액티비티
 *
 * UI 요소가 화면에 표시되는 시점을 추적하는 것은 분석 로깅, UI 상태 관리, 동영상 콘텐츠 자동 재생 또는
 * 일시중지를 통한 리소스 최적화 등 다양한 사용 사례에 유용합니다.
 * Compose는 다음과 같은 UI 요소 공개 상태를 추적하기 위한 여러 수정자를 제공합니다.
 *
 * onVisibilityChanged - 이 수정자는 컴포저블의 표시 상태가 변경될 때 알림을 보냅니다. 컴포저블이 표시될 때마다 작업이나 부작용을 트리거하는 데 적합합니다.
 * onLayoutRectChanged - 이 수정자는 루트, 창, 화면을 기준으로 컴포저블의 경계에 관한 정보를 제공합니다. 하위 수준 제어를 제공하며
 * onVisibilityChanged의 기본 API입니다. 이 수정자는 onGloballyPositioned와 유사하지만 성능이 더 우수하고 유연성이 향상되었습니다.
 */
class TrackingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 콘텐츠가 시스템 바 뒤로 흐르도록 설정(Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ComposeTheme {
                Surface(
                    modifier = Modifier
                        .systemBarsPadding(),
                    color = Color.White
                ) {
                    FullscreenScreen()
                }
            }
        }
    }

    @Composable
    private fun FullscreenScreen(){
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
//            TextVisibilityChangedTest()
            TextVisibilityChangedTest2()
        }
    }

    /**
     * 3초 후 텍스트 백그라운드 색상 변경 샘플
     */
    @Composable
    private fun TextVisibilityChangedTest() {

        // 1. 배경색을 관리할 상태 선언 (기본은 투명이나 원하는 색상)
        var backgroundColor by remember { mutableStateOf(Color.Transparent) }

        // 배경색 변경을 위한 타겟 색상 결정
        val animatedBgColor by animateColorAsState(
            targetValue = backgroundColor,
            animationSpec = tween(durationMillis = 500) // 0.5초 동안 부드럽게 전환
        )

        Text(
            text = "Some text",
            modifier = Modifier
                .onVisibilityChanged(minDurationMs = 3000) { visible ->
                    if (visible) {
                        // Do something if visible
                        Log.d("TrackingActivity", "visible true")
                        backgroundColor = Color.Yellow
                    } else {
                        // Do something if not visible
                        Log.d("TrackingActivity", "visible false")
                    }
                }
                .background(animatedBgColor)    // 아래 padding 과 순서를 바꾸면 패딩이 먼저 적용되고 텍스트의 배경색이 지정됨(노란색 영역이 줄어듦)
                .padding(vertical = 8.dp)
        )
    }

    data class TextState(
        val text: String = "Some text",
        val color: Color = Color.Transparent,
        val isVisible: Boolean = false
    )

    /**
     * textState 를 저장하는 객체에 text view 표시 정보를 저장하여 변경 표시하는 샘플
     */
    @Composable
    private fun TextVisibilityChangedTest2() {

        // 1. 배경색을 관리할 상태 선언 (기본은 투명이나 원하는 색상)
        var textState by remember { mutableStateOf(TextState()) }

        // 배경색 변경을 위한 타겟 색상 결정
        val animatedBgColor by animateColorAsState(
            targetValue = textState.color,
            animationSpec = tween(durationMillis = 500) // 0.5초 동안 부드럽게 전환
        )

        Text(
            text = "Some text",
            modifier = Modifier
                .onVisibilityChanged(minDurationMs = 3000) { visible ->
                    textState = textState.copy(
                        isVisible = visible,
                        color = if(visible){
                            Log.d("TrackingActivity", "visible true")
                            Color.Yellow
                        } else {
                            // Do something if not visible
                            Log.d("TrackingActivity", "visible false")
                            Color.Transparent
                        }
                    )

                }
                .background(animatedBgColor)
                .padding(vertical = 8.dp)
        )
    }
}



