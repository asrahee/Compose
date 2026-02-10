package com.example.compose.ui.main.flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme

/**
 * FLowRow, FlowColumn 샘플
 *
 * 설명 : Row 및 Column 과 유사한 컴포저블이지만, 공간이 부족하면 다음 줄로 흐른다는 점이 다름.
 * 이렇게 하면 여러 행이나 열이 생성되는데 maxItemsInEachRow 또는 maxItemsInEachColumn을 설정하여
 * 한 줄에 표시되는 항목 수를 제어할 수도 있다.
 * maxItemsInEach*와 Modifier.weight(weight)를 함께 사용하면 필요할 때 행 또는
 * 열의 너비를 채우거나 확장하는 레이아웃을 빌드할 수 있습니다
 */
class FlowActivity : ComponentActivity() {
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
        FlowRowSimpleUsageExample()
    }
}

@Composable
private fun FlowRowSimpleUsageExample(){
    FlowRow(modifier = Modifier.padding(8.dp)) {
        ChipItem("Price: High to Low")
        ChipItem("Avg rating: 4+")
        ChipItem("Free breakfast")
        ChipItem("Free cancellation")
        ChipItem("£50 pn")
    }
}

@Composable
fun ChipItem(text: String) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}