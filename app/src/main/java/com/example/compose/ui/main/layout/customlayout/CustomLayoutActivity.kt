package com.example.compose.ui.main.layout.customlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme

/**
 * layout 수정자를 사용하여 요소가 측정되고 배치되는 방식을 수정할 수 있습니다.
 * Layout은 람다입니다. 매개변수에는 측정할 수 있는 요소(measurable로 전달됨) 및
 * 이 컴포저블의 수신된 제약 조건(constraints로 전달됨)이 포함됩니다.
 */
class CustomLayoutActivity : ComponentActivity() {
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
            TextWithPaddingToBaselinePreview()

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black))

            TextWithNormalPaddingPreview()

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black))

            TestMyBasicColumn()
        }
    }


    @Preview
    @Composable
    fun TextWithPaddingToBaselinePreview() {
        Text("Hi there 1111!", Modifier.firstBaselineToTop(32.dp))
    }

    @Preview
    @Composable
    fun TextWithNormalPaddingPreview() {
        Text("Hi there 22222!", Modifier.padding(top = 32.dp))
    }

    /**
     * "지금 이 Modifier가 붙은 컴포저블이 베이스라인 정보를 가지고 있는지 확인해라.
     * 만약 'Unspecified(정해지지 않음)'라면,
     * 이 레이아웃 계산은 진행할 수 없으니 즉시 에러를 발생시켜라."
     */
    @Composable
    fun Modifier.firstBaselineToTop(
        firstBaselineToTop: Dp
    ) = layout { measurable, constraints ->
        // Measure the composable
        /**
         * placeable은 컴포저블의 측정이 끝난 결과물입니다. 여기에 대괄호([])를 써서 특정 **정렬선(Alignment Line)**의 위치를 물어볼 수 있습니다.
         */
        val placeable = measurable.measure(constraints)

        /**
         * 베이스라인이 없는 컴포저블(예: 단순 이미지 등)에 이 Modifier를 쓰면 에러를 내서 잘못된 사용을 방지합니다.
         *
         * 코틀린 표준 라이브러리 함수인 check는 인자로 받은 조건이 false일 경우 IllegalStateException을 발생시킵니다.
         * "이 수정자(Modifier)는 반드시 텍스트처럼 베이스라인 정보가 있는 컴포저블에만 사용해야 한다"는 전제 조건을 강제하는 것입니다.
         * 만약 이미지(Image)나 단순한 박스(Box)에 이 수정자를 적용하면,
         * 베이스라인 값이 없으므로 앱이 실행 중에 에러를 내며 개발자에게 잘못된 사용임을 즉시 알려줍니다.
         */
        // AlignmentLine.Unspecified 가 의미하는 것
        // 모든 컴포저블이 베이스라인을 가지고 있지는 않습니다.
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        // 측정된 컴포저블(텍스트 등)의 상단 끝에서 실제 베이스라인까지의 현재 거리입니다.
        val firstBaseline = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable gets placed
            placeable.placeRelative(0, placeableY)
        }
    }

    @Composable
    private fun TestMyBasicColumn(modifier: Modifier = Modifier) {
        MyBasicColumn(modifier.padding(8.dp)) {
            Text("MyBasicColumn")
            Text("places items")
            Text("vertically.")
            Text("We've done it by hand!")
        }
    }

    /**
     * 맞춤 레이아웃 만들기
     * layout 수정자는 호출하는 컴포저블만 변경합니다. 여러 컴포저블을 측정하고 배치하려면 Layout 컴포저블을 대신 사용하세요.
     *
     * layout 수정자와 마찬가지로 measurables는 측정해야 하는 하위 요소 목록이며 constraints는
     * 상위 요소의 제약 조건입니다. 앞서와 동일한 로직에 따라 MyBasicColumn을 다음과 같이 구현할 수 있습니다.
     */
    @Composable
    fun MyBasicColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->
            // Don't constrain child views further, measure them with given constraints
            // List of measured children
            val placeables = measurables.map { measurable ->
                // Measure each children
                measurable.measure(constraints)
            }

            // Set the size of the layout as big as it can
            layout(constraints.maxWidth, constraints.maxHeight) {
                // Track the y co-ord we have placed children up to
                var yPosition = 0

                // Place children in the parent layout
                placeables.forEach { placeable ->
                    // Position item on the screen
                    placeable.placeRelative(x = 0, y = yPosition)

                    // Record the y co-ord placed up to
                    yPosition += placeable.height
                }
            }
        }
    }
}





