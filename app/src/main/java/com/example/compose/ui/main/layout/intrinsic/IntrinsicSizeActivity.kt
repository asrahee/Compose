package com.example.compose.ui.main.layout.intrinsic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme

/**
 * 내장 기능 측정
 *
 * Compose 규칙 중 하나는 하위 요소를 한 번만 측정해야 한다는 것입니다.
 * 하위 요소를 두 번 측정하면 런타임 예외가 발생합니다.
 * 하지만 하위 요소를 측정하기 전에 하위 요소에 관한 정보가 필요한 경우도 있습니다.
 *
 * 컴포저블에 IntrinsicSize.Min 또는 IntrinsicSize.Max를 요청할 수 있습니다.
 *
 * Modifier.width(IntrinsicSize.Min) - 콘텐츠를 적절하게 표시하는 데 필요한 최소 너비는 얼마인가요?
 * Modifier.width(IntrinsicSize.Max) - 콘텐츠를 적절하게 표시하는 데 필요한 최대 너비는 무엇인가요?
 * Modifier.height(IntrinsicSize.Min) - 콘텐츠를 적절하게 표시하는 데 필요한 최소 높이는 무엇인가요?
 * Modifier.height(IntrinsicSize.Max) - 콘텐츠를 적절하게 표시하는 데 필요한 최대 높이는 무엇인가요?
 */
class IntrinsicSizeActivity : ComponentActivity() {
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
            Text("일반 Row 에 텍스트, 구분선 표시")
            TwoTexts(text1 = "text1", text2 = "text2")

            Spacer(modifier = Modifier.height(10.dp))

            Text("레이아웃 측정 measurePolicy > IntrinsicMeasureScope 사용")
            MyCustomComposable(content = { TwoTexts(text1 = "text1", text2 = "text2")  })

            Spacer(modifier = Modifier.height(10.dp))

            MyCustomComposable {
                TwoTexts(text1 = "첫 번째 줄", text2 = "왼쪽/오른쪽")
                TwoTexts(text1 = "두 번째 줄", text2 = "다시 가로로")
            }
        }
    }

    /**
     * 구분선으로 구분된 두 텍스트를 화면에 표시하는 컴포저블
     */
    @Composable
    private fun TwoTexts(
        modifier: Modifier = Modifier, text1: String, text2: String
    ){
        // 콘텐츠를 적절하게 표시하는 데 필요한 최소 높이 설정
        /**
         * Row의 높이는 다음과 같이 결정됩니다.
         *
         * Row 컴포저블의 minIntrinsicHeight는 하위 요소의 최대 minIntrinsicHeight입니다.
         * Divider 요소의 minIntrinsicHeight는 제약 조건이 주어지지 않으면 공간을 차지하지 않으므로 0입니다.
         * Text minIntrinsicHeight은 특정 width의 텍스트입니다.
         * 따라서 Row 요소의 height 제약 조건은 Text의 최대 minIntrinsicHeight가 됩니다.
         * 그런 다음 Divider가 height를 Row가 지정한 height 제약 조건으로 확장합니다.
         */
        Row(modifier = modifier.height(IntrinsicSize.Min)){
            Text(
                modifier = modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .wrapContentWidth(Alignment.Start),
                text = text1
            )

            VerticalDivider(
                color = Color.Black,
                modifier = Modifier.fillMaxHeight().width(1.dp)
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .wrapContentWidth(Alignment.End),
                text = text2
            )
        }
    }

    /**
     * 맞춤 Layout의 내장 기능 측정을 지정하려면 만들 때 MeasurePolicy 인터페이스의
     * minIntrinsicWidth,
     * minIntrinsicHeight,
     * maxIntrinsicWidth,
     * maxIntrinsicHeight를 재정의하세요.
     */
    @Composable
    private fun MyCustomComposable(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            content = content,
            modifier = modifier,
            measurePolicy = object : MeasurePolicy {
                override fun MeasureScope.measure(
                    measurables: List<Measurable>,
                    constraints: Constraints
                ): MeasureResult {
                    // 1. 자식 요소들을 측정 (Measure)
                    val placeables = measurables.map { it.measure(constraints) }

                    // 2. 부모 레이아웃의 크기 결정 (여기서는 자식들의 폭 중 최대값, 높이의 총합)
                    val width = placeables.maxOfOrNull { it.width } ?: constraints.minWidth
                    val height = placeables.sumOf { it.height }

                    // 3. 레이아웃 배치 (Layout)
                    return layout(width, height) {
                        var yPosition = 0
                        placeables.forEach { placeable ->
                            // 자식들을 위에서 아래로 차례대로 배치
                            placeable.placeRelative(x = 0, y = yPosition)
                            yPosition += placeable.height
                        }
                    }
                }

                // 고정 높이에서 최소로 필요한 폭을 계산하는 로직 (선택 사항)
                override fun IntrinsicMeasureScope.minIntrinsicWidth(
                    measurables: List<IntrinsicMeasurable>,
                    height: Int
                ): Int {
                    // 가장 넓은 자식의 최소 폭을 반환
                    return measurables.maxOfOrNull { it.minIntrinsicWidth(height) } ?: 0
                }
            }
        )
    }
}



