package com.example.compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.example.compose.ui.main.FullscreenActivity
import com.example.compose.ui.main.FullscreenActivityCompose
import com.example.compose.ui.main.flow.FlowActivity
import com.example.compose.ui.main.pager.HorizontalPagerActivity
import com.example.compose.ui.theme.ComposeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 메인 액티비티 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                // 루트 컨테이너
                // 시각적 배경 + Material 스타일 속성 + 터치/클리핑 기반" 제공
                // 배경, 모양, 그림자, 클립, Ripple 영역 등을 결정
                Surface(
                    modifier = Modifier
//                        .statusBarsPadding()        // enableEdgeToEdge() 효과로 상단 표시줄 영역까지 컨텐츠가 확장되어 보여지는 현상 방지
//                        .safeDrawingPadding()           // 컨텐츠가 가려지지 않은 안전한 영역 안에 배치(상단 표시줄, 하단 네비게이션 바 영역)
                        .systemBarsPadding()        // 상하단 시스템 바를 모두 피해서 여백을 줌
                        .fillMaxSize()
                        .background(Color.White),    // 전체 화면 배경 흰색
                        color = Color.White         // Material 배경도 흰색
                ) {
                    // 화면을 구성할 때 자주 쓰이는 UI 요소(TopBar, BottomBar, FAB, Drawer)를 표준화된 레이아웃 안에서 배치해 주는 컨테이너
                    Scaffold(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)) { innerPadding ->

                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())  // 스크롤 가능하도록 설정
                            .background(Color.White)) {
//
//                            Greeting(
//                                name = "Android",
//                                modifier = Modifier.padding(innerPadding)
//                            )
//
//                            // 일반 버튼
                            FilledButtonExample {
                                // 풀 화면 appcompat activity 호출
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        FullscreenActivity::class.java
                                    )
                                )
                            }

                            FilledTonalButtonExample {
                                // 풀 화면 compose activity 호출
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        FullscreenActivityCompose::class.java
                                    )
                                )
                            }

                            OutlineButtonExample {
                                // compose HorizontalPager 액티비티 호출
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        HorizontalPagerActivity::class.java
                                    )
                                )
                            }

                            ElevatedButtonExample {
                                // compose HorizontalPager 액티비티 호출
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        FlowActivity::class.java
                                    )
                                )
                            }
//                            TextButtonExample {}
//
//                            // 플로팅 버튼
//                            FloatingExample {}
//                            FloatingSmallExample {}
//                            LargeFloatingExample {}
//                            ExtendedExample {}
//
//                            // 분할 선택 버튼
//                            SingleChoiceSegmentedButton()
//                            MultiChoiceSegmentedButton()
//
//                            // 카드
//                            CardMinimalExample()
//                            filledCardExample()
//                            ElevatedCardExample()
//                            OutlinedCardExample()
//
//                            // 체크박스
//                            CheckboxMinimalExample()
//                            CheckboxParentExample()
//
//                            // 칩
//                            AssistChipExample()
//                            FilterChipExample()
//                            InputChipExample("입력칩"){}
//
//                            // 날짜 선택 도구
//                            DatePickerDocked()
//                            DatePickerFieldToModal()
////                            DatePickerModalInput({}, {})
//
//                            // 다어얼로그
////                            DialogExample()
////                            MinimalDialog{}
////                            SampleDialogWithImage()
//
//                            // 구분선
//                            HorizontalDividerExample()
//                            VerticalDividerExample()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        color = Color.Black,
        modifier = modifier
    )
}

/******************************************** 버튼  *******************************************/
/** 채워진 버튼 */
@Composable
fun FilledButtonExample(onClick: () -> Unit){
    Button(onClick = { onClick()}) {
        Text("FullscreenActivity(Filled Button)")
    }
}

/** 채워진 톤 버튼 */
@Composable
fun FilledTonalButtonExample(onClick: () -> Unit) {
    FilledTonalButton(onClick = { onClick() }) {
        Text("FullscreenActivityCompose(Tonal Button)")
    }
}

/** 윤곽선 있는 버튼 */
@Composable
fun OutlineButtonExample(onClick: () -> Unit) {
    OutlinedButton(onClick = { onClick() } ) {
        Text("HorizontalPagerActivity(Outlined Button)")
    }
}

/** 돌출 버튼 */
@Composable
fun ElevatedButtonExample(onClick: () -> Unit) {
    ElevatedButton(onClick = { onClick() },
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 20.dp,      // 기본은 1dp → 크게 키우면 효과가 두꺼워짐
            pressedElevation = 30.dp,     // 눌렀을 때 더 두껍게
            focusedElevation = 8.dp,
            hoveredElevation = 6.dp,
            disabledElevation = 0.dp
        )) {
        Text("FlowActivity(Elevated Button)")
    }
}

/** 텍스트 버튼(누를 때까지는 텍스트로만 표시됨) */
@Composable
fun TextButtonExample(onClick: () -> Unit){
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent, // 평소 기본 상태
            contentColor = Color.Black
        )
    ){
        Text("Text Button")
    }
}

/******************************************** 플로팅 버튼  *******************************************/
/** 플로팅 작업 버튼 */
@Composable
fun FloatingExample(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(Icons.Filled.Add, "Floating actoin button.")
    }
}

/** 작은 플로팅 작업 버튼 */
@Composable
fun FloatingSmallExample(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor =  MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, "Small floating action button")
    }
}

/** 큰 사이즈 플로팅 작업 버튼 */
@Composable
fun LargeFloatingExample(onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.Add, "Large floating action button")
    }
}

/** 확장된 플로팅 작업 버튼 */
@Composable
fun ExtendedExample(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
        text = { Text(text = "Extended FAB") },
    )
}


/******************************************** 분류 버튼  *******************************************/
/** 싱글 선택 분할 버튼 */
@Composable
fun SingleChoiceSegmentedButton(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Day", "Month", "Week")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}

/** 다중 선택 분할 버튼 */
@Composable
fun MultiChoiceSegmentedButton(modifier: Modifier = Modifier){
    val selectedOptions = remember {
        mutableStateListOf(false, false, false)
    }
    val options = listOf("Walk", "Ride", "Drive")

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->

            var checked = selectedOptions[index]

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                checked = checked,
                onCheckedChange = {
                    selectedOptions[index] = !selectedOptions[index]
                },
                // 선택, 미선택에 따라 아이콘/텍스트 색상 표시 설정 -> 부분 버튼 전체 영역에 적용됨
//                colors = SegmentedButtonDefaults.colors(
//                    activeContainerColor = Color.Blue,
//                    inactiveContentColor = Color.Gray
//                ),

                // checked('selectedOptions[index]') 상태에 따라 자동으로 다른 UI 를 그려주는 기본 아이콘 구성 요소
                // -> 이 SegmentedButton 이 아이콘을 그릴 때 현재 index 의 선택 여부에 따라 기본 제공 아이콘 스타일을 자동으로 표시하라는 의미
//                icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) },

                // 체크 아이콘(왼쪽) 완전 커스텀
                icon = {
                    SegmentedButtonDefaults.Icon(
                        active = checked,
                        activeContent = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Checked",
                                tint = Color.Magenta   // 체크 아이콘 색 여기서 마음대로
                            )
                        },
                        inactiveContent = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Unchecked",
                                tint = Color.LightGray
                            )
                        }
                    )
                },

                label = {
                    when (label) {
                        "Walk" -> Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = "Directions Walk",
//                            // 아이콘의 색상 적용(체크되면 파란색, 아니면 회색으로 표시)
//                            tint = if(selectedOptions[index]) Color.Blue else Color.Gray
                        )
                        "Ride" -> Icon(
                            imageVector =
                                Icons.Default.DirectionsBus,
                            contentDescription = "Directions Bus",
//                            // 아이콘의 색상 적용(체크되면 파란색, 아니면 회색으로 표시)
//                            tint = if(selectedOptions[index]) Color.Blue else Color.Gray
                        )
                        "Drive" -> Icon(
                            imageVector =
                                Icons.Default.DirectionsCar,
                            contentDescription = "Directions Car",
//                            // 아이콘의 색상 적용(체크되면 파란색, 아니면 회색으로 표시)
//                            tint = if(selectedOptions[index]) Color.Blue else Color.Gray
                        )
                    }
                }
            )
        }
    }
}

/******************************************** 카드  *******************************************/
/**
 * 카드(기본적으로 콘텐츠를 Column 컴포저블에 래핑하여 각 항목을 아래 카드 내에 배치함)
 * 유의할 주요 매개변수
 * elevation :
 * colors :
 * enabled : 이 매개변수에 false 를 전달하면 카드가 사용 중지된 것으로 표시됨. 사용자 입력에 응답하지 않음
 * onClick : 일반적으로 Card 는 클릭 이벤트 미허용. onClick 매개변수를 정의하는 오버로드를 사용하여 사용자 누름에 응답하게 할 수 있음 -> 실험기능(베타)
 * */
@Composable
fun CardMinimalExample() {
    Card() {
        Text(text = "Hello, world!")
    }
}

/** 채워진 카드 */
@Composable
fun filledCardExample() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Filled",
            modifier = Modifier
                .padding(10.dp) // margin 역할(Text 바깥 여백)
                .background(
                    color = Color.Green,
                    shape = RoundedCornerShape(12.dp)
                )
//                .padding(16.dp),    // padding 역할(텍스트 내부 여백)
                .padding(horizontal = 12.dp, vertical = 6.dp),    // padding 역할(텍스트 내부 여백)
            textAlign = TextAlign.Center,
        )
    }
}

/** 돌출 카드 */
@Composable
fun ElevatedCardExample(){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 30.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary, // 카드 배경
            contentColor = Color.Red // 카드 내부
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 150.dp)
    ) {
        Text(
            text = "Elevated",
            color = Color.Black,
            modifier = Modifier
                .background(
                    color = Color.Gray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

/** 윤곽선 카드 */
@Composable
fun OutlinedCardExample() {
    OutlinedCard(
        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.Yellow),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Outlined",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}


/******************************************** 캐러셀  *******************************************/
// 창 크기에 따라 동적으로 스크롤 가능한 항목 목록 표시(간단한 텍스트도 표시 가능)
// https://developer.android.com/develop/ui/compose/components/carousel?hl=ko 참고


/******************************************** 체크박스 *******************************************/
/** 단순 체크 박스 */
@Composable
fun CheckboxMinimalExample() {
    var checked by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Minimal checkbox"
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }

    Text(
        if (checked) "Checkbox is checked" else "Checkbox is unchecked"
    )
}

/** 전체 체크 박스 체크 기능 */
@Composable
fun CheckboxParentExample() {
    // Initialize states for the child checkboxs
    val childCheckedStates = remember { mutableStateListOf(false, false, false) }

    // Compute the parent state based on children's sates
    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        // Parent TriStateCheckbox
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Select all")
            TriStateCheckbox(
                state = parentState,
                onClick = {
                    // Determine new state based on current state
                    val newState = parentState != ToggleableState.On
                    childCheckedStates.forEachIndexed { index, _ ->
                        childCheckedStates[index] = newState
                    }
                }
            )
        }

        // Child Checkboxs
        childCheckedStates.forEachIndexed { index, checked ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Optinos ${index + 1}")
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        // Update the individual child state
                        childCheckedStates[index] = isChecked
                    }
                )
            }
        }
    }

    if(childCheckedStates.all { it }){
        Text("All options selected")
    }
}


/******************************************** 칩(콤팩트한 대화형 UI : 아이콘 + 라벨)  *******************************************/
/** 지원 칩 */
@Composable
fun AssistChipExample(){
    AssistChip(
        onClick = { Log.d("Assist chip", "hello world")},
        label = { Text("Assist chip")},
        leadingIcon = {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Localized description",
                Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}

/** 필터 칩 */
@Composable
fun FilterChipExample() {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        onClick = { selected = !selected },
        label = {
            Text("Filter chip")
        },
        selected = selected,
        leadingIcon = if(selected){
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

/** 입력 칩 */
@Composable
fun InputChipExample(
    text: String,
    onDismiss: () -> Unit
) {
    var enabled by remember { mutableStateOf(true) }
    if(!enabled) return

    InputChip(
        onClick = {
            onDismiss()
            enabled = !enabled
        },
        label = { Text(text) },
        selected = enabled,
        avatar = {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        }
    )
}

/** 추천 칩 - 동적으로 생성된 힌트를 표시(정보제공 목적) */
@Composable
fun SuggestionChipExample(){
    SuggestionChip(
        onClick = { Log.d("Suggestion chip", "hello world")},
        label = { Text("Suggestion chip")}
    )
}


/******************************************** 날짜 선택 도구 *******************************************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(){
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text("DOB") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if(showDatePicker){
            Popup (
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long) : String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun DatePickerFieldToModal(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it) } ?: "",
        onValueChange = { },    // 사용자의 입력을 막음 -> 대신 날짜 선택 UI 를 띄우기 위함
        label = { Text("DOB") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {  // 한 번의 제스처(터치 다운 > 이동 > 업)을 하나의 블록으로 처리하는 구조
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.

                    // PointerEventPass 의 3단계
                    // Initial -> Main -> Final(컴포넌트가 이벤트를 받기 전 단계 / 컴포넌트가 실제 이벤트를 소비하는 단계 / 소비되고 난 후)
                    // Clickable 같은 modifier 는 Min pass 이후 에 붙어 있어도 클릭이 무시되는 문제가 있음,
                    // 그래서 Initial pass 에서 먼저 터치를 가로채 클릭을 인식하는 방식
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)  // 손가락이 떼어질 때(up)까지 기다림, 취소되면 null 반환
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
){
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick =  {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/******************************************** 대화 상자 *******************************************/
/** 얼럿 다이얼로그 */
@Composable
fun DialogExample(){
    val openAlertDialog = remember { mutableStateOf(true) }

    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    print("Confirmation registred")
                },
                dialogTitle = "Alert dialog example",
                dialogText = "This is an example of an alert dialog with buttons.",
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector
){
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

/** 기본 다이얼로그 */
@Composable
fun MinimalDialog(onDismissRequest: () -> Unit){
    Dialog(onDismissRequest = { onDismissRequest() }){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ){
            Text(
                text = "This is a minimal dialog",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

// 고급 다이얼로그 버튼 클릭 시 다이얼로그 닫기 처리
@Composable
fun SampleDialogWithImage(){
    var showDialog by remember { mutableStateOf(true) }

    if(showDialog){
        DialogWithImage(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showDialog = false
            },
            painter = painterResource(R.drawable.ic_launcher_foreground),
            imageDescription = "샘플 이미지"
        )
    }
}

/** 고급 다이얼로그 */
@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String
){
    Dialog(onDismissRequest = { onDismissRequest() }){
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(160.dp)
                )

                Text(
                    text = "This is a dialog with buttons and an image",
                    modifier = Modifier
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Dismiss")
                    }

                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


/******************************************** 가로 구분선  *******************************************/
/** 가로 구분선 예 */
@Composable
fun HorizontalDividerExample() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)    // spacedBy 하위 composable 사이의 간격을 균등하게 설정(패딩과 다름, 아이템 사이의 간격만 설정)
    ){
        Text("First item in list")
        HorizontalDivider(thickness = 2.dp)
        Text("Second item in list")
    }
}

/** 세로 구분선 예 */
@Composable
fun VerticalDividerExample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),                 // Row 의 높이를 자식들의 가장 작은 필요 높이로 맞춤
        horizontalArrangement = Arrangement.SpaceEvenly // Row 내부의 children 사이, 양 끝까지 동일한 간격으로 배치(SpaceBetween, SpaceAround 등의 옵션이 더 있음)
    ){
        Text("First item in row")
        VerticalDivider(color = MaterialTheme.colorScheme.secondary)
        Text("Second item in row")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeTheme {
        Greeting("Android")
    }
}

