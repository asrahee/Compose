package com.example.compose.ui.main.layout.constraint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme

/**
 * ConstraintLayout은 화면에 다른 컴포저블을 기준으로 컴포저블을 배치할 수 있는 레이아웃입니다.
 * 여러 중첩된 Row, Column, Box, 맞춤 레이아웃 요소 대신 사용할 수 있습니다.
 * ConstraintLayout은 더 복잡한 정렬 요구사항이 있는 더 큰 레이아웃을 구현할 때 유용합니다.
 *
 * 다음 시나리오에서는 ConstraintLayout을 사용하는 것이 좋습니다.
 *
 * 코드 가독성 개선을 위해 화면에 요소를 배치하는 여러 Column 및 Row를 중첩하지 않습니다.
 * 다른 컴포저블을 기준으로 컴포저블을 배치하거나 가이드라인, 배리어, 체인을 기반으로 컴포저블을 배치합니다.
 *
 * Compose에서 ConstraintLayout을 사용하려면 build.gradle에 이 종속 항목을 추가해야 합니다(Compose 설정에도).
 *
 *
 * implementation "androidx.constraintlayout:constraintlayout-compose:$constraintlayout_compose_version"
 *
 */
class ConstraintLayoutActivity : ComponentActivity() {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ConstraintLayoutContent()
            DecoupledConstraintLayout()
            ConstraintLayoutContentBarrier()
            ConstraintChainExample()
        }
    }

    /**
     * Compose의 ConstraintLayout은 DSL을 사용하여 다음과 같은 방식으로 작동합니다.
     *
     * createRefs() 또는 createRefFor()를 사용하여 ConstraintLayout에서 각 컴포저블의 참조를 만듭니다.
     * 제약 조건은 constrainAs() 수정자를 사용하여 제공됩니다.
     * 이 수정자는 참조를 매개변수로 사용하고 본문 람다에 제약 조건을 지정할 수 있게 합니다.
     * 제약 조건은 linkTo() 또는 다른 유용한 메서드를 사용하여 지정됩니다.
     * parent는 ConstraintLayout 컴포저블 자체에 대한 제약 조건을 지정하는 데 사용할 수 있는 기존 참조입니다.
     */
    @Composable
    private fun ConstraintLayoutContent(){
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Create guideline from the start of the parent at 10% the width of the Composable
            val startGuideline = createGuidelineFromStart(0.1f)
            // Create guideline from the end of the parent at 10% the width of the Composable
            val endGuideline = createGuidelineFromEnd(0.1f)
            //  Create guideline from 16 dp from the top of the parent
            val topGuideline = createGuidelineFromTop(16.dp)
            //  Create guideline from 16 dp from the bottom of the parent
            val bottomGuideline = createGuidelineFromBottom(16.dp)

            // Create references for the composable to constrain
            val (button, text) = createRefs()

            Button(
                onClick = { /** Do something */ },
                // Assing reference "button" to the Button composable
                // and constrain it to the top of the ConstraintLayout
                modifier = Modifier.constrainAs(button){
//                    top.linkTo(parent.top, margin = 16.dp)
                    // 또는
                    // 버튼을 상단 가이드라인과 왼쪽 가이드라인에 연결
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                    // 가이드 라인 덕분에 부모 끝까지 안 가고 10% 지점에서 멈춤
                    end.linkTo(endGuideline)

                    // 폭을 가이드라인 사이에 맞게 꽉 채우고 싶다면
                    width = Dimension.fillToConstraints
                    // 2. 세로: 콘텐츠 크기에 맞춤 (이게 빠지면 세로로 늘어날 수 있음)
                    height = Dimension.wrapContent
                }
            ){
                Text("Button", textAlign = TextAlign.Center)
            }

            // Assign reference "text" to the Text composable
            // and constrain it to the bottom of the Button composable
            Text(
                "Text",
                Modifier.constrainAs(text){
//                    top.linkTo(button.bottom, margin = 16.dp)
                    // 3. 텍스트를 버튼 아래, 그리고 가이드라인 중심에 배치
                    top.linkTo(button.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)

                    // 아래쪽 가이드라인에도 연결하고 싶다면
                    bottom.linkTo(bottomGuideline)

                    // 만약 아래쪽 여백이 너무 많이 남으면 수직 바이어스를 줄 수 있습니다.
//                    verticalBias = 0.2f
                }
            )
        }
    }

    /**
     * 제약 조건 분리하기
     *
     * ConstraintLayout 예에서 제약 조건은 적용되는 컴포저블의 수정자와 함께 인라인으로 지정됩니다.
     * 그러나 제약 조건이 적용되는 레이아웃에서 제약 조건을 분리하는 것이 더 좋은 상황이 있습니다.
     * 예를 들어 화면 구성을 기반으로 제약 조건을 변경하거나 두 제약 조건 세트 사이에 애니메이션을 적용할 수 있습니다.
     *
     * 이 같은 경우에는 ConstraintLayout을 서로 다른 방식으로 사용할 수 있습니다.
     *
     * ConstraintSet을 매개변수로 ConstraintLayout에 전달합니다.
     * layoutId 수정자를 사용하여 ConstraintSet에 생성된 참조를 컴포저블에 할당합니다.
     */
    @Composable
    private fun DecoupledConstraintLayout(){
        BoxWithConstraints {
            val constraints = if (minWidth < 600.dp) {
                decoupledConstraints(margin = 16.dp) // Portrait constraints
            } else {
                decoupledConstraints(margin = 32.dp) // Landscape constraints
            }

            ConstraintLayout(constraints) {
                Button(
                    onClick = { },
                    modifier = Modifier.layoutId("button")
                ){
                    Text("Button")
                }

                Text("Text", Modifier.layoutId("text"))
            }
        }
    }

    /**
     * 그러면 제약 조건을 변경해야 할 때 다른 ConstraintSet을 전달하기만 하면 됩니다.
     */
    private fun decoupledConstraints(margin: Dp): ConstraintSet {
        return ConstraintSet {
            val button = createRefFor("button")
            val text = createRefFor("text")

            constrain(button){
                top.linkTo(parent.top, margin = margin)
            }
            constrain(text){
                top.linkTo(button.bottom, margin)
            }
        }
    }

    /**
     * 배리어는 여러 컴포저블을 참조하여 지정된 쪽에서 가장 극단적인 위젯을 기반으로 가상 가이드라인을 만듭니다.
     *
     * 배리어를 만들려면 createTopBarrier()
     * (또는 createBottomBarrier(), createEndBarrier(), createStartBarrier())를 사용하고,
     * 배리어를 구성해야 하는 참조를 제공합니다.
     */
    @Composable
    private fun ConstraintLayoutContentBarrier() {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val startGuideline = createGuidelineFromStart(0.1f)
            val endGuideline = createGuidelineFromEnd(0.1f)
            val topGuideline = createGuidelineFromTop(50.dp) // 약간 아래로 배치

            val (button, text, title) = createRefs()

            // 1. Barrier 생성: button과 text 중 더 위쪽(Top) 끝을 기준으로 장벽을 만듦
            // 이렇게 하면 두 요소 중 하나가 위로 밀려 올라가도 장벽이 따라 올라갑니다.
            val topBarrier = createTopBarrier(button, text)

            Button(
                onClick = { },
                modifier = Modifier.constrainAs(button) {
                    top.linkTo(topGuideline) // 기준점
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text("Button")
            }

            Text(
                "Main Text Content",
                Modifier.constrainAs(text) {
                    top.linkTo(button.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                }
            )

            // 2. Barrier 활용: 새로운 Title 요소를 Barrier 위쪽에 배치
            Text(
                "Section Title",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.constrainAs(title) {
                    // 이 텍스트의 바닥(bottom)을 barrier의 상단에 연결
                    bottom.linkTo(topBarrier, margin = 8.dp)
                    start.linkTo(startGuideline)
                }
            )
        }
    }

    @Composable
    private fun ConstraintChainExample(){
        // 1. ConstraintSet 정의
        val constraintSet = ConstraintSet {
            val button = createRefFor("button")
            val text = createRefFor("text")

            // 수직 체인 생성: 버튼과 텍스트를 위아래로 묶고 Spread 스타일 적용
            // Spread는 남은 공간을 요소들 사이에 균등하게 배분합니다.
            createVerticalChain(button, text, chainStyle = ChainStyle.Spread)

            // 수평 체인 생성 : 버튼과 텍스트가 가로 중앙에 오도록 설정
            createHorizontalChain(button, text, chainStyle = ChainStyle.Packed)

            // 각 요소의 세부 제약 조건 (폭과 높이 등)
            constrain(button){
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                // 가로 체인을 부모 중앙에 맞추기 위해 start/end 연결
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            constrain(text) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent

//                // 1. 텍스트의 상단을 버튼의 상단에 연결
//                top.linkTo(button.top)
//                // 2. 텍스트의 하단을 버튼의 하단에 연결
//                bottom.linkTo(button.bottom)

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        }

        // 2. ConstraintLayout에 적용
        ConstraintLayout(
            constraintSet = constraintSet,

            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.layoutId("button") // ID 매칭
            ) {
                Text("Button")
            }

            Text(
                text = "Text content in Chain",
                modifier = Modifier.layoutId("text") // ID 매칭
            )
        }
    }
}



