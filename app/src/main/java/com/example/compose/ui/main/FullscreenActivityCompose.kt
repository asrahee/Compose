package com.example.compose.ui.main

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

class FullscreenActivityCompose : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 콘텐츠가 시스템 바 뒤로 흐르도록 설정(Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FullscreenScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FullscreenScreen(viewModel: FullscreenViewModel = viewModel()){
        // 현재 이 컴포저블(함수)이 실행되고 있는 환경의 **Context**를 가져옵니다.
        // 기존 액티비티 내부에서 this를 호출하는 것과 유사합니다.
        val context = LocalContext.current
        val window = (context as Activity).window
        // Compose 콘텐츠가 그려지고 있는 **최상위 뷰(Root View)**를 가져옵니다.
        val view = LocalView.current
        // 시스템 바(상태바, 내비게이션 바)를 넣고 뺄 수 있는 **'리모컨'(WindowInsetsController)**을 만듭니다.
        // 구글에서 제공하는 하위 호환용 도구입니다. 최신 폰이든 옛날 폰이든 똑같은 코드로 상태바를 숨길 수 있게 도와줍니다.
        val insetsController = remember { WindowCompat.getInsetsController(window, view) }

        // ViewModel의 상태를 관찰
        val isFullscreen by viewModel.isFullscreen
        val isControlsVisible by viewModel.isControlsVisible

        // 시스템 UI 제어 로직 (상태 변화에 따라 반응)
        /** LaunchedEffect는 Jetpack Compose에서 **"특정 상태가 바뀌었을 때, UI 그리기 이외의 작업(부수 효과)을 안전하게 실행하기 위해 사용하는 도구"**입니다.
        1. 왜 그냥 함수 안에 코드를 쓰면 안 되나요?
        Compose 함수(컴포저블)는 데이터가 바뀔 때마다 **수시로, 아주 빠르게 다시 호출(리컴포지션)**됩니다. 만약 LaunchedEffect 없이 아래처럼 코드를 짜면 문제가 생깁니다.

        2. 작동 원리: Key (열쇠)
        LaunchedEffect(key1, key2, ...)는 소괄호 안에 들어가는 Key값이 핵심입니다.
        최초 실행: 컴포저블이 화면에 처음 나타날 때 블록 내 코드가 실행됩니다.
        Key 변경 시: key1이나 key2의 값이 이전과 달라지면, 기존에 실행 중이던 코루틴을 취소하고 새로운 코루틴을 시작합니다.
        종료 시: 컴포저블이 화면에서 사라지면 실행 중이던 작업도 자동으로 취소됩니다.

        3. 주요 사용 사례
        ① 처음 켜질 때 딱 한 번만 실행하고 싶을 때
        Key 자리에 고정값인 Unit이나 true를 넣습니다.
        ② 특정 상태에 반응해야 할 때 (질문하신 Fullscreen 사례)
        ③ 지연 작업 (타이머)
         **/
        LaunchedEffect(isFullscreen) {
            if (isFullscreen) {
                // 상태바(Status Bar)와 내비게이션 바(Navigation Bar)를 모두 숨깁니다.
                // systemBars()는 위쪽 시간/배터리가 나오는 줄과 아래쪽 뒤로가기/홈 버튼이 있는 줄을 합친 개념
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE:
                //"화면 끝을 쓸어 넘기면(Swipe) 잠시 나타났다가 자동으로 다시 사라지게 하라"는 뜻입니다.
                //유튜브나 넷플릭스 같은 전체 화면 모드에서 흔히 보는 동작이죠.
                //이 설정을 안 하면, 한 번 숨긴 바를 다시 꺼내기가 매우 번거로워집니다.
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                // 숨겼던 시스템 바들을 다시 화면에 표시합니다.
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                // 상태바 아이콘을 흰색으로 강제 (배경이 검은색일 때 필수)
                insetsController.isAppearanceLightStatusBars = false
            }
        }

        // 초기 실행 시 100ms 후 자동으로 숨기기 (기존 onPostCreate 역할)
        LaunchedEffect(Unit) {
            delay(100)
            viewModel.toggleFullscreen()
        }

        // 전체 화면 상태일 때 뒤로가기를 누르면 전체 화면만 해제하기
        /**
         * 처음 백키 동작 시 isFullscreen 이 false 로 되면서 이제 핸들러는 "사용 안 함" 상태가 된다.
         * 두 번쩨 백키 동작 시 현재 컴포저블의 BackHandler 가 비활성 상태이므로, 시스템은 그 다음 우선순위(예) 상위 NavHose의 기본도작)
         * 을 찾아 이전 화면으로 이동하게 된다.
         */
        BackHandler(enabled = isFullscreen) {
            viewModel.setFullscreen(false)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { viewModel.toggleFullscreen() } // 전체 화면 터치 시 토글
        ) {
            // 1. 메인 콘텐츠 (기존 TextView 역할)
            Text(
                text = "Fullscreen Content",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium
            )

            // 2. 하단 컨트롤 영역 (기존 LinearLayout 영역)
            /**
             * AnimatedVisibility 의 역할
             * 이 컴포저블은 단순히 요소를 보여주고 숨기는 것을 넘어, **"상태 변화에 따른 자연스러운 전환(Transition)"**을 담당합니다.
             * 조건부 렌더링 + 애니메이션: visible = isControlsVisible 값에 따라 내부 UI를 컴포지션에 추가하거나 제거합니다. 이때 enter와 exit 속성에 지정한 fadeIn, slideInVertically 등을 결합해 부드러운 시각적 효과를 만들어냅니다.
             * 레이아웃 흐름 관리: 요소가 완전히 사라지면(Exit 완료) 레이아웃에서 공간을 차지하지 않도록 깔끔하게 제거해 줍니다.
             * 사용자 경험(UX): 풀스크린 모드에서 컨트롤바가 갑자기 툭 나타나는 게 아니라, 아래에서 위로 스르륵 올라오는 효과를 주어 앱의 완성도를 높여주는 역할을 합니다.
             *
             * 중첩(부모 + 자식) 사용 시
             * 결론부터 말씀드리면 네, 둘 다 정상적으로 동작합니다. 하지만 두 애니메이션이 동시(Parallel) 혹은 **순차적(Nested)**으로 얽히기 때문에 시각적으로 어떻게 보일지를 이해하는 것이 중요합니다.
             *
             * Compose의 AnimatedVisibility는 중첩(Nested)될 경우 상위(부모)의 상태에 종속적인 구조를 가집니다.
             *
             *  1. 동작 원리: "부모가 먼저, 자식은 그다음"
             * 부모와 자식에 모두 AnimatedVisibility가 있다면 다음과 같은 흐름으로 동작합니다.
             * 나타날 때 (Appearing)
             * 부모 체크: 부모의 visible이 true가 되어야만 자식 컴포저블이 컴포지션(트리)에 포함됩니다.
             * 부모 애니메이션 시작: 부모가 지정한 enter 효과(예: FadeIn)가 시작됩니다.
             * 자식 애니메이션 시작: 자식의 visible도 true라면, 부모가 나타나는 도중에 자식의 enter 효과(예: SlideIn)가 함께 일어납니다.
             *
             * 사라질 때 (Disappearing)
             * 부모가 false가 되면: 부모는 자식에게 "이제 우리 사라져야 해"라고 알립니다.
             * 동시 진행: 부모와 자식의 exit 애니메이션이 동시에 수행됩니다.
             * 제거: 모든 애니메이션(부모와 자식 모두)이 끝나야만 실제 UI 트리에서 해당 요소들이 완전히 제거됩니다.
             */
            AnimatedVisibility(
                visible = isControlsVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                        /* 버튼 클릭 로직 */
                            viewModel.toggleFullscreen()
                        },
                        modifier = Modifier.pointerInput(Unit) {
                            // 터치 시 자동 숨김 예약 (기존 delayHideTouchListener 역할)
                            detectTapGestures(onPress = {
                                // 필요한 경우 여기서 타이머 초기화 로직 추가
                            })
                        }
                    ) {
                        Text("DUMMY BUTTON")
                    }
                }
            }
        }
    }
}