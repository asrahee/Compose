package com.example.compose.ui.main.pager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.FilledButtonExample
import com.example.compose.OutlineButtonExample
import com.example.compose.ui.theme.AnimatedOrderedListViewModel
import com.example.compose.ui.theme.ComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * lazy column, HorizontalPager 샘플 액티비티
 */
class HorizontalPagerActivity : ComponentActivity() {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenScreen(){
    // 가장 간단한 형태의 lazy list
//    lazyColumExample()

    // 아이템 제거, 추가 애니메이션 lazy colum
//    AnimatedOrderedListScreen()

    HorizontalPager()
}


/** 기본 lazy column 샘플 */
@Composable
fun lazyColumExample(){
    LazyColumn {
        // Add a single item
        item {
            Text(text = "First item")
        }

        // 기존 Column 의 내용을 표시하려면 LazyColumn 의 특정 아이템으로 표시해야 한다.
        item {
            FilledButtonExample(){}
        }

        // Add 5 items
        items(5) { index ->
            Text(text = "Item: $index")
        }

        // Add another single item
        item {
            Text(text = "Last item")
        }
    }
}

/**
 * 리스트 추가, 제거, 종류별 정렬 버튼 샘플/
 */
@Composable
fun AnimatedOrderedListScreen(
    viewModel: AnimatedOrderedListViewModel = viewModel(),
    modifier: Modifier = Modifier
){
    val displayedItems by viewModel.displayedItems.collectAsStateWithLifecycle()

    ListAnimatedItemsExample(
        displayedItems,
        onAddItem = viewModel::addItem,
        onRemoveItem = viewModel::removeItem,
        resetOrder = viewModel::resetOrder,
        onSortAlphabetically = viewModel::sortAlphabetically,
        onSortByLength = viewModel::sortByLength,
        modifier = modifier
    )
}

// 항목을 추가 및 삭제하고 사전 정의된 목록을 정렬하는 컨트롤이 통합된 화면을 보여준다
@Composable
private fun ListAnimatedItemsExample(
    data: List<String>,
    modifier: Modifier = Modifier,
    onAddItem: () -> Unit = {},
    onRemoveItem: () -> Unit = {},
    resetOrder: () -> Unit = {},
    onSortAlphabetically: () -> Unit = {},
    onSortByLength: () -> Unit = {},
) {
    val canAddItem = data.size < 16
    val canRemoveItem = data.isNotEmpty()

    // 정렬 옵션을 선택 했을 경우에만 lazy list 를 맨 위로 스크롤 처리
    // 1. 스크롤 상태를 여기서 생성(Hoisting)
    val listState = rememberLazyListState()
    // 2. 코루틴 스코프 생성(버튼 클릭 시 suspend 함수를 실행하기 위함)
    val coroutineScope = rememberCoroutineScope()

    // 리스트의 최상단 아이템으로 스크롤 이동
    val scrollToTop = {
        coroutineScope.launch {
            if(data.isNotEmpty()){
                // 해당 위치로 바로 스크롤
//                listState.scrollToItem(0)
                // 부드럽게 스크롤
                listState.animateScrollToItem(0)
            }
        }
    }

    // 리스트의 마지막 아이템으로 스크롤 이동
    val scrollToBottom = {
        coroutineScope.launch {
            if(data.isNotEmpty()){
                listState.animateScrollToItem(data.size - 1)
            }
        }
    }

    Scaffold(modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Buttons that change the value of displayedItems.
            AddRemoveButtons(canAddItem, canRemoveItem
                , onAddItem = {
                    onAddItem()
                    scrollToBottom()
                }
                , onRemoveItem = {
                    onRemoveItem()
                    scrollToBottom()
                })

            // 3. 정렬 버튼 콜백에 scrollToTop 을 결합하여 전달
            OrderButtons(
                resetOrder = {
                    resetOrder()
                    scrollToTop()
                },

                orderAlphabetically = {
                    onSortAlphabetically()
                    scrollToTop()
                },

                orderByLength = {
                    onSortByLength()
                    scrollToTop()
                })

            // List that displays the values of displayedItems.
            // 4. 리스트 상태 전달
            ListAnimatedItems(data, listState = listState)
        }
    }
}

@Composable
fun ListAnimatedItems(
    items: List<String>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState() // 외부에서 주입받음
) {

//    // 1. 스크롤 상태를 관리하는 state 생성
//    val listState = rememberLazyListState()
//
//    // 2. items의 사이즈가 변경될 때마다 실행되는 Effect
//    // items.size를 키값으로 두어 데이터 개수가 변할 때만 트리거됩니다.
//    LaunchedEffect(items.size) {
//        if (items.isNotEmpty()) {
//            // 맨 처음(index 0) 아이템으로 부드럽게 스크롤
//            listState.animateScrollToItem(items.size - 1)
//        }
//    }

    LazyColumn(
        modifier = modifier,
        state = listState   // 주입받은 상태를 연결
    ) {
        stickyHeader { Header() }

        // Use a unique key per item, so that animations work as expected.
        items(items, key = { it }) {
            ListItem(
                headlineContent = { Text(it) },
                modifier = Modifier
                    .animateItem(
                        // Optionally add custom animation specs
                    )
                    .fillParentMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 0.dp),
            )
        }
    }

    /**
     * 첫 번째 아이템을 지나쳤는지 체크하고 특정 동작을 하려는 경우 아래와 같이 사용
     *
     * 왜 이렇게 복잡하게 쓰나요? (이유와 장점)
     * 만약 snapshotFlow를 쓰지 않고 일반적인 컴포저블 본문에서 인덱스를 체크한다면,
     * 사용자가 스크롤할 때마다 **수백 번의 리컴포지션(Recomposition)**이 발생하여 앱이 버벅거릴 수 있습니다.
     *
     * 이 방식의 장점:
     * 성능 최적화: distinctUntilChanged 덕분에 실제로 상태가 "변화"했을 때만 로직이 실행됩니다.
     * 비동기 처리: 스크롤 감지와 분석 데이터 전송을 메인 UI 스레드 방해 없이 코루틴 스코프 내에서 처리합니다.
     * 정확성: "사용자가 리스트 상단을 떠났다"는 시점을 딱 한 번만 포착할 수 있습니다.
     *
     * 4. 실전 활용 예시
     * 이 로직은 분석 서비스 전송 외에도 다음과 같은 곳에 자주 쓰입니다.
     * 사용자가 아래로 내리면 '맨 위로 가기' 버튼을 보여줄 때
     * 헤더의 디자인을 축소형으로 바꿀 때
     * 특정 지점까지 읽었을 때 '읽음 처리'를 할 때
     */
    LaunchedEffect(listState) {
        // snapshotFlow { ... }는 이 블록 안에서 참조하는 상태값이 바뀔 때마다 그 값을 Flow 데이터 스트림으로 내보냅니다.
        // listState.firstVisibleItemIndex는 Compose의 MutableState입니다. 이 값은 스크롤할 때마다 수시로 변합니다.
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            // 매우 중요한 최적화 단계입니다. 값이 이전과 다를 때만 아래로 데이터를 보냅니다.
            // 예: 사용자가 계속 아래로 스크롤해서 인덱스가 1, 2, 3...으로 변해도 결과는 계속 true, true, true입니다.
            // 이때 이 연산자는 첫 번째 true만 통과시키고 나머지는 차단합니다.
            .distinctUntilChanged()
            // 값이 true인 경우(즉, 첫 번째 아이템을 완전히 지나친 순간)에만 데이터를 통과시킵니다.
            .filter { it }
            // .collect 를 호출해야 비로소 감시가 시작됨

            // LaunchedEffect 안에서 snapshotFlow 를 사용하는 이유
            // collect 는 suspend function 으로 . 함수는 코루틴 안에서 실행되어야 하면, 완료될 때까지 해당 코루틴을 점유한다
            // 컴포저블 자체는 중단 함수가 아니므로 collect 를 직접 쓸 수 없다. LaunchedEffect 를 사용하면
            // 컴포저블이 화면에서 사라질 때(onCleared) 자동을로 감시(Flow 수집)을 중단한다. 만일 LaunchedEffect
            // 없이 감시를 시작할 수 있다면, 화면을 나간 후에도 계속 메모리를 소모하여 스크롤을 감시하는 '좀비 프로세스'
            // 가 된다.
            .collect {
                Log.d("ListAnimatedItems", "첫 번째 아이템 지나침")
            }
    }
}

@Composable
private fun Header(){
    // 스티키 헤더 표시를 위해 설정을 추가함
    Surface(
        modifier = Modifier
//            .fillMaxWidth()
            .zIndex(1f), // 다른 아이템보다 위에 그려지도록 설정
//        color = MaterialTheme.colorScheme.surface, // 배경색을 불투명하게 지정
//        tonalElevation = 4.dp // 살짝 그림자를 주면 더 명확히 구분됩니다
    ) {
        OutlineButtonExample {}
    }
}

@Composable
private fun AddRemoveButtons(
    canAddItem: Boolean,
    canRemoveItem: Boolean,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(enabled = canAddItem, onClick = onAddItem) {
            Text("Add Item")
        }
        Spacer(modifier = Modifier.padding(25.dp))
        Button(enabled = canRemoveItem, onClick = onRemoveItem) {
            Text("Delete Item")
        }
    }
}

@Composable
private fun OrderButtons(
    resetOrder: () -> Unit,
    orderAlphabetically: () -> Unit,
    orderByLength: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Reset", "Alphabetical", "Length")

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = {
                        Log.d("AnimatedOrderedList", "selectedIndex: $selectedIndex")
                        selectedIndex = index
                        when (options[selectedIndex]) {
                            "Reset" -> resetOrder()
                            "Alphabetical" -> orderAlphabetically()
                            "Length" -> orderByLength()
                        }
                    },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun HorizontalPager(){
    val pageItemSize = 10
    val pagerState = rememberPagerState(pageCount = { pageItemSize })
    val coroutineScope = rememberCoroutineScope ()

    // HorizontalPager 및 VerticalPager은 플링 동작이 한 번에 한 페이지씩 스크롤할 수 있는 최대 페이지 수를 설정
    // 빠르게 스크롤 할 경우 적용됨
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(pageItemSize)
    )

    // 자동 진행 페이지 만들기
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val pageInteractionSource = remember { MutableInteractionSource() }
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

    // Stop auto-advancing when pager is dragged or one of the page is pressed
    val autoAdvance = !pagerIsDragged && !pageIsPressed

    // 드래그 하거나 눌림 상태가 아니라면 2초마다 다음 페이지로 이동 처리
    // autoAdvance 상태가 바뀔 때마다 LaunchedEffect가 재실행되거나 취소됩니다.
    if(autoAdvance){
        LaunchedEffect(pagerState, pageInteractionSource) {
            while(true){
                delay(2000)
                // 페이지 이동 중 예외 발생(예: 사용자가 갑자기 터치 등)을 대비해 try-catch를 쓰기도 합니다.
                runCatching {
                    val nextPage = (pagerState.currentPage + 1) % pageItemSize
                    // animateScrollToPage가 State를 수정하면,
                    // 그 State를 읽고 있던 HorizontalPager가 자동으로 반응하여 스스로를 다시 그리는 구조(Snapshot 시스템)
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        /**
         * pagerState 에는 다음과 같은 페이지에 관한 정보가 포함되어 있다.
         *
         * currentPage: 스냅 위치에 가장 가까운 페이지입니다. 기본적으로 스냅 위치는 레이아웃의 시작 부분에 있습니다.
         * settledPage: 애니메이션이나 스크롤이 실행되지 않을 때의 페이지 번호입니다. 이는 currentPage 속성과 다릅니다. currentPage는 페이지가 스냅 위치에 충분히 가까우면 즉시 업데이트되지만 settledPage는 모든 애니메이션이 실행될 때까지 동일하게 유지됩니다.
         * targetPage: 스크롤 이동의 제안된 중지 위치입니다.
         */
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do Something with each page change, for example : viewModel.sendPageSelectedEvent(page)
            Log.d("HorizontalPager", "page changed to $page")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager (
//    VerticalPager(state = pagerState) { page ->
            state = pagerState,

            // 1. 좌우 패딩을 주어 다음/이전 페이지가 보일 공간을 확보
            contentPadding = PaddingValues(horizontal = 24.dp),
            // 2. 페이지 사이의 간격을 설정
            pageSpacing = 10.dp,
            // 3. 선택사항) 현재 카드가 항상 중앙에 오도록 정렬
            verticalAlignment = Alignment.CenterVertically,

            // 현재 화면 밖의 미리 로드할 페이지 설정
            beyondViewportPageCount = 1,

            flingBehavior = fling,

            // 맞춤 페이지 크기(고정)
//            pageSize = PageSize.Fixed(100.dp),
            // 표시 영역 크기에 따라 페이지 크기를 조정하려면 맞춤 페이지 크기 계산을 사용
//            pageSize = threePagesPerViewport,

            modifier = Modifier
                .fillMaxWidth()
//                .weight(1f) // 버튼을 아래로 밀어내고 남은 공간 차지

        ) { page ->
            // 1. Box를 추가하여 카드를 중앙에 정렬합니다.
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(),
////                    .padding(start = 32.dp),
//                contentAlignment = Alignment.Center // 자식인 Card를 정렬,
//            ) {

            // HorizontalPager 에서 contentPadding 을 주어 Box 로 감싸지 않아도 가운데 정렬됨
            Card(
                Modifier
//                        .size(200.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        /**
                         * lerp는 **Linear Interpolation(선형 보간)**의 약자로, 두 값 사이의 관계를 직선적으로 계산하여 그 사이에 위치한 값을 찾아내는 방식입니다.
                         *
                         * start: 시작 값 (0% 지점)
                         * stop: 종료 값 (100% 지점)
                         * fraction: 진행 정도 (0.0 ~ 1.0 사이의 값)
                         *
                         * 왜 lerp를 쓰는 걸까?
                         * 단순히 if-else를 쓰면 값이 갑자기 툭툭 변하게 되지만, lerp를 사용하면 다음과 같은 장점이 있습니다.
                         * 부드러운 전환: 수치가 소수점 단위로 아주 세밀하게 변하므로 애니메이션이 끊김 없이 부드럽습니다.
                         * 직관적인 설계: "0에서 1로 변할 때 투명도는 0.5에서 1로 변한다"는 관계를 코드로 명확하게 표현할 수 있습니다.
                         * 다양한 타입 지원: 숫자뿐만 아니라 Color(lerp(Color.Red, Color.Blue, 0.5f))나 Dp, Offset 등 다양한 UI 요소에 적용 가능합니다.
                         */
                        lerp(
                            start = 0.85f, // 보조 페이지는 약간 더 작게 보이도록 수정 가능
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,   // 가장 흐릴 때
                            stop = 1f,  // 가장 선명할 때
                            fraction = 1f - pageOffset.coerceIn(0f, 1f) // 스크롤 진행도에 따른 역산
                        )
                    }
            ) {
                // Card content
                Text(
                    text = "Page :$page",
                    color = Color.Yellow,
                    modifier = Modifier.fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Black)
                        .clickable(
                            interactionSource = pageInteractionSource,
                            indication = LocalIndication.current
                        ) {
                            Log.d("HorizontalPager", "$page clicked")
                        }
                )
            }

//            }
        }

        // 페이지 인디케이터 컴포저블
        PagerIndicator(pageItemSize, pagerState.currentPage)

        Button(onClick = {
            coroutineScope.launch {
                pagerState.scrollToPage(5)
                // 부드럽게 스크롤 하고자 할 때
//                pagerState.animateScrollToPage(5)
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.CenterHorizontally)
        ){
            Text("Jump to Page 5")
        }
    }
}

/**
 * HorizontalPager, VerticalPager 의 인디케이터 표시
 */
@Composable
fun PagerIndicator(pageCount:Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier){
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (currentPageIndex == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }
    }
}

/**
 * 표시 영역 크기에 따라 페이지 크기를 조정하려면 맞춤 페이지 크기 계산을 사용
 */
private val threePagesPerViewport = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace - 2 * pageSpacing) / 3
    }
}