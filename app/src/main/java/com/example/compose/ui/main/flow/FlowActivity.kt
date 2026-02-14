package com.example.compose.ui.main.flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.compose.ui.theme.ComposeTheme
import com.google.android.material.color.MaterialColors

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

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(15.dp)
        ){
            FlowRowSimpleUsageExample()
            FlowRowExample()
            FlowRowExample2()
            FlowRowExample3()
            FlowRowExample4()
        }
    }

    /**
     * 수동 flow row 생성
     */
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

    /**
     * 임의 갯수의 flow row 생성(동일 가중치)
     */
    @Composable
    fun FlowRowExample(){
        val rows = 3
        val columns = 3

        FlowRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = rows
        ) {
            val itemModifier = Modifier
                .padding(4.dp)
                .height(80.dp)
                .weight(1f) // 각 행에서 1/3 씩 공간 차지
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF90CAF9))
            repeat(rows * columns) {
                Spacer(modifier = itemModifier)
            }
        }
    }

    /**
     * 임의 갯수의 flow row 생성(가중치 변경)
     */
    @Composable
    fun FlowRowExample2(){
        FlowRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 2
        ){
            val itemModifier = Modifier
                .padding(4.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Blue)

            repeat(6){ item ->
                if((item + 1) % 3 == 0){
                    Spacer(modifier = itemModifier.fillMaxWidth())
                } else {
                    Spacer(modifier = itemModifier.weight(0.5f))
                }
            }
        }
    }

    /**
     * 부분 크기 조정
     */
    @Composable
    fun FlowRowExample3(){
        FlowRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 3
        ) {
            val itemModifier = Modifier
                .clip(RoundedCornerShape(8.dp))
            Box(
                modifier = itemModifier
                    .height(200.dp)
                    .width(60.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = itemModifier
                    .height(200.dp)
                    // Modifier.fillMaxWidth(fraction)를 사용하면 상품이 차지해야 하는 컨테이너의 크기를 지정
                    // 전체 컨테이너 너비의 0.7 비율을 가짐
                    .fillMaxWidth(0.7f)
                    .background(Color.Blue)
            )
            Box(
                modifier = itemModifier
                    .height(200.dp)
                    // 1, 2번째 Box 를 제외한 나머지 너비 영역
                    .weight(1f)
                    .background(Color.Magenta)
            )
        }
    }

    /**
     * fillMaxColumnWidth() 및 fillMaxRowHeight()
     * 동일한 열 또는 행의 항목이 열/행에서 가장 큰 항목과 동일한 너비 또는 높이를 차지하게 설정
     */
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun FlowRowExample4(){
        val listDesserts = listOf("apple", "Banana", "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "IceCream Sandwich", "Jellybean", "KitKat", "Lollipop", "Marshmallow", "Nougat")
        FlowColumn(
            Modifier
                .padding(20.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachColumn = 5,
        ) {
            repeat(listDesserts.size) {
                Box(
                    Modifier
                        .fillMaxColumnWidth()
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {

                    Text(
                        text = listDesserts[it],
                        fontSize = 18.sp,
                        modifier = Modifier.padding(3.dp)
                    )
                }
            }
        }

    }
}




