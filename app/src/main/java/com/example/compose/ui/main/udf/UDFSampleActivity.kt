package com.example.compose.ui.main.udf

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 간단한 UDF(Unidirectional Data Flow의 약자로, 우리말로 번역하면 **'단방향 데이터 흐름'**이라는 뜻) 샘플
 *
 * 1. UDF의 핵심 구조
 * UDF 아키텍처에서 데이터는 오직 한 방향으로만 흐릅니다.
 * 상태(State)는 아래로: ViewModel(부모)에서 UI(자식) 방향으로 흐릅니다.
 * 이벤트(Event)는 위로: UI(자식)에서 ViewModel(부모) 방향으로 흐릅니다.
 *
 * 2. 왜 UDF를 쓰나요? (장점)
 * 예전 방식(데이터가 위아래로 마구 섞여 흐르는 방식)에 비해 크게 3가지 장점이 있습니다.
 * 데이터 일관성 (Testability): 화면에 표시되는 상태의 출처가 단 하나(Single Source of Truth)이기 때문에, "왜 화면에 이 값이 떠 있지?"라는 의문이 생길 때 ViewModel의 상태만 확인하면 됩니다.
 * 버그 감소: UI가 직접 상태를 수정하지 못하고 오직 '이벤트'만 전달하기 때문에, 예상치 못한 곳에서 상태가 변해 발생하는 버그가 획기적으로 줄어듭니다.
 * UI 분리: UI는 단순히 "받은 상태를 그리기만 하는 역할"에 집중할 수 있어 코드가 훨씬 단순해집니다.
 */
class UDFSampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier
                    .systemBarsPadding(),
                color = Color.White
            ) {
                MainScreen()
            }
        }
    }


    @Composable
    fun MainScreen(viewModel: MainViewModel = viewModel()) {
//        val state by viewModel.state // 상태 구독(mutableStateOf 사용 시)

        val context = LocalContext.current
        val state by viewModel.state.collectAsStateWithLifecycle()

        // Side Effect 처리 전용 블록
        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is MainEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                    is MainEffect.NavigateToDetail -> {
                        // navController.navigate("detail") 등의 로직
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "현재 카운트: ${state.count}", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.testTag("테스트 태그"),
                    onClick = { viewModel.handleIntent(MainIntent.ClickIncrement) }) {
                    Text("증가")
                }
                Button(onClick = { viewModel.handleIntent(MainIntent.ClickReset) }) {
                    Text("리셋")
                }
            }
        }
    }
}

/**
 * 이 설계의 장점 (왜 이렇게 하나요?)
 * 예측 가능한 상태: 화면에 버그가 생기면 MainState 객체만 조사하면 됩니다. 상태가 여기저기 흩어져 있지 않기 때문입니다.
 * 단일 진입점: ViewModel에 handleIntent라는 문 하나만 열어두기 때문에, 어떤 액션이 들어오는지 추적하기가 매우 쉽습니다. (로그 찍기 최적!)
 * 테스트 용이성: IncrementUseCase는 UI 없이도 순수 코틀린 테스트 코드로 검증할 수 있습니다. 이것이 Clean Architecture의 핵심인 관심사 분리입니다.
 *
 * 왜 이렇게 쓰나요? (Use Case 패턴의 정석)
 * Clean Architecture에서 UseCase를 정의할 때 이 방식을 쓰는 이유는 코드의 가독성 때문입니다.
 * 장점:
 * 직관적임: UseCase는 보통 "단 하나의 행동(Action)"을 수행합니다. 따라서 객체 자체가 그 행동의 이름이 되고, 이를 함수처럼 호출하는 것이 "증가(5)"와 같이 읽혀서 훨씬 자연스럽습니다.
 * 구현체 숨기기: 내부 함수 이름이 execute인지 perform인지 신경 쓸 필요 없이, 무조건 ()로 실행하면 된다는 규약을 만들 수 있습니다.
 */

// UseCase: 숫자를 증가시키는 비즈니스 규칙
class IncrementUseCase {
    /**
     * 코틀린에는 **연산자 오버로딩(Operator Overloading)**이라는 기능이 있습니다.
     * 특정 이름을 가진 함수 앞에 operator를 붙이면, 이를 약속된 기호나 문법으로 대체할 수 있게 해줍니다.
     *
     * plus() 함수에 operator를 붙이면 + 기호를 쓸 수 있습니다.
     * invoke() 함수에 operator를 붙이면 () (호출 기호)를 쓸 수 있습니다.
     */

    /**
     * invoke는 특별한 이름을 가진 약속된 함수입니다. 클래스 내에 invoke 함수를 정의하면,
     * 해당 클래스의 인스턴스를 마치 함수인 것처럼 ()를 붙여 호출할 수 있습니다.
     *
     * 일반적인 호출: useCase.execute(count)
     * invoke 호출: useCase(count) (함수 이름 없이 객체 이름 뒤에 바로 괄호를 붙임)
     */
    operator fun invoke(currentCount: Int): Int = currentCount + 1
}

// State: 화면에 보여줄 모든 정보를 담은 단 하나의 객체
data class MainState(
    val count: Int = 0,
    val isLoading: Boolean = false
)

// Intent(Event): 사용자가 행할 수 있는 액션들
sealed class MainIntent {
    object ClickIncrement : MainIntent()
    object ClickReset : MainIntent()
}

// UI에서 발생할 일회성 사건들
sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    object NavigateToDetail : MainEffect()
}

class MainViewModel(
    private val incrementUseCase: IncrementUseCase = IncrementUseCase()
) : ViewModel() {

    // 외부에서는 읽기만 가능한 하나의 상태
//    private val _state = mutableStateOf(MainState())
//    val state: State<MainState> = _state

    // 1. State 관리 (이전과 동일)
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    // 2. Effect 관리 (Channel 사용)
    private val _effect = Channel<MainEffect>()
    val effect = _effect.receiveAsFlow() // 외부에서는 Flow로 관찰

    // Intent를 처리하는 단일 진입점
    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.ClickIncrement -> {
                val newCount = incrementUseCase(_state.value.count)
                _state.value = _state.value.copy(count = newCount)

                // 특정 조건에서 Side Effect 발생
                if (newCount % 10 == 0) {
                    viewModelScope.launch {
                        _effect.send(MainEffect.ShowToast("10단위 달성! 축하합니다."))
                    }
                }
            }
            is MainIntent.ClickReset -> {
                _state.value = _state.value.copy(count = 0)

                viewModelScope.launch {
                    _effect.send(MainEffect.ShowToast("초기화되었습니다."))
                }
            }
        }
    }
}

/**
 * 요약
 * State: 화면의 현재 상태 (예: 점수, 이름, 로딩 여부) → StateFlow
 * Intent: 사용자의 액션 (예: 클릭, 입력) → Function Call
 * Effect: 한 번만 일어나는 사건 (예: 토스트, 이동) → Channel
 *
 * 이 구조를 갖추면 안드로이드에서 가장 강력하고 디버깅하기 쉬운 아키텍처를 완성하게 됩니다!
 */