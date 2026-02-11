package com.example.compose.ui.main.adaptivelayout

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.example.compose.ui.theme.ComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Android 16(API 수준 36) 에서는 기기 방향, 화면 비율, 디스플레이 크기에 관한 앱 제한을 재정의 하여 앱이 다양한 폼 팩터와
 * 디스플레이 크기에 적응할 수 있다. 재정의는 최소 너비가 600dp 이상인 기기에 적용되며 다음을 정의함
 *
 * 태블릿
 * 대형 화면 폴더블의 내부 디스플레이
 * 데시크톱 창 모드(모든 폼 팩터)
 *
 * API 수준 36을 타겟팅하는 앱은 디스플레이의 최소 너비가 600dp 이상인 경우 크기를 조절할 수 있고, 멀티 윈도우 모드
 * (resizeableActiity = "tru" 와 동일)로 전환할 수 있음
 *
 * API 수준 36 동작을 선택 해제하려면 PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY 매니페스트 속성을 선언
 * - 애플리케이션 또는 특정 액티비티 AndroidManifest.xml 에 선언
 *     <property
 *         android:name="android.window.PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY"
 *         android:value="true" />
 * - API 수준 37 이상을 타겟팅 하는 경우 항상 무시됨
 *
 */

/**
 * Compose Material 3 적응형 라이브러리
 * Compose Material 3 적응형 라이브러리는 적응형 앱 개발을 용이하게 하는 구성요소와 API를 제공합니다.
 *
 * ✓ 권장사항
 * 다음 API를 사용하여 앱을 적응형으로 만듭니다.
 *
 * NavigationSuiteScaffold: 앱 창 크기 클래스에 따라 탐색 메뉴와 탐색 레일 간에 전환합니다.
 * ListDetailPaneScaffold: 목록-세부정보 표준 레이아웃을 구현합니다. 레이아웃을 앱 창 크기에 맞게 조정합니다.
 * SupportingPaneScaffold: 지원 창 표준 레이아웃을 구현합니다.
 */
class AdaptiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 콘텐츠가 시스템 바 뒤로 흐르도록 설정(Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 기기가 폴딩을 지원하는지 여부를 검사
        checkIsFolding()

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

        /**
         * 사용법 2 : collectAsStateWithLifecycle 를 사용하여 State(상태)값을 사용
         * 이렇게 하면 컴포저블이 화면에서 사라질 때 데이터 수집도 자동으로 멈춰 안전합니다.
         */
//        // 1. 레이아웃 정보를 상태로 변환하여 구독 (Lifecycle에 안전함)
//        val layoutInfo by WindowInfoTracker.getOrCreate(LocalContext.current)
//            .windowLayoutInfo(LocalContext.current)
//            .collectAsStateWithLifecycle(initialValue = null)
//
//        // 2. 상태 계산 (DerivedStateOf를 써서 최적화 가능)
//        val isTableTop = remember(layoutInfo) {
//            val foldFeature = layoutInfo?.displayFeatures
//                ?.filterIsInstance<FoldingFeature>()
//                ?.firstOrNull()
//            isTableTopPosture(foldFeature)
//        }

        Column(
            modifier = Modifier.padding(8.dp)
        ) {

        }
    }

    /**
     * 사용법 1 : 참고용, 실제론 FullscreenScreen() 안의 사용법 2 사용 하거나 해당 값을 viewModel 에 저장하여 사용
     */
    private fun checkIsFolding(){
        // 앱에서 접힌 상태 인식
        lifecycleScope.launch(Dispatchers.Main) {
            // Safely collects from WindowInfoTracker when the lifecycle is
            // STARTED and stops collection when the lifecycle is STOPPED.
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@AdaptiveActivity)
                    .windowLayoutInfo(this@AdaptiveActivity)
                    .collect { layoutInfo ->
                        // Use layoutInfo to update the layout.
                        // New posture information.
                        /**
                         * FoldingFeature bounds 속성 (DisplayFeature에서 상속됨)은 접힘 또는 힌지와 같은 접기 기능의 경계 직사각형을 나타냅니다.
                         * 경계는 기능을 기준으로 화면에 요소를 배치하는 데 사용할 수 있습니다.
                         */
                        val foldingFeature = layoutInfo.displayFeatures
                            .filterIsInstance<FoldingFeature>()
                            .firstOrNull()
                        // Use information from the foldingFeature object.

                        val isTableTopPosture = isTableTopPosture(foldingFeature)
                        Log.d("AdaptiveActivity", "isTableTopPosture $isTableTopPosture")
                    }
            }
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun isTableTopPosture(foldFeature : FoldingFeature?) : Boolean {
        contract { returns(true) implies (foldFeature != null) }
        return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
                foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
    }
}





