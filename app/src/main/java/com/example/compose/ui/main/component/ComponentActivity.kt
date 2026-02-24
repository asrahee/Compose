package com.example.compose.ui.main.component

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 *
 */
class ComponentSampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            // 바텀 시트 샘플
//            BottomSheetDemo()

            // 부분 바텀 시트 샘플
            PartialBottomSheet()
        }
    }

    /**
     * 바텀 시트 샘플
     *
     * 프로그래매틱 방식으로 시트를 확장하고 축소하려면 SheetState를 사용합니다.
     * rememberModalBottomSheetState를 사용하여 sheetState 매개변수로 ModalBottomSheet에 전달해야 하는 SheetState 인스턴스를 만들 수 있습니다.
     * SheetState를 통해 현재 시트 상태와 관련된 속성뿐만 아니라 show 및 hide 함수에도 액세스할 수 있습니다.
     * 이러한 정지 함수는 CoroutineScope가 필요하고(예: rememberCoroutineScope 사용) UI 이벤트에 대한 응답으로 호출할 수 있습니다.
     * 바텀 시트를 숨길 때 컴포지션에서 ModalBottomSheet를 삭제해야 합니다.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomSheetDemo() {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }

        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show bottom sheet") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                    onClick = {
                        showBottomSheet = true // 시트를 보여줌
                    }
                )
            }
        ) { contentPadding ->
            // 1. 화면의 메인 콘텐츠
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Main Screen Content")
            }

            // 2. 조건부 바텀시트 노출 (이 로직만 남겨야 합니다)
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // 바텀시트 내부 콘텐츠
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("This is the Bottom Sheet content")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    sheetState.hide() // 1. 숨기기 애니메이션 시작
                                }.invokeOnCompletion {
                                    /**
                                     * 애니메이션이 끝나서 Job이 완료 상태(Completed)가 되면 등록된 람다 블록을 실행합니다.
                                     *
                                     * invokeOnCompletion은 코루틴이 취소되거나 에러가 나도 실행됩니다.
                                     * 그래서 안전하게 if (!sheetState.isVisible) 같은 체크를 한 번 더 해주는 것이 좋습니다.
                                     */
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false // 2. 애니메이션 끝난 후 제거
                                    }
                                }
                            }
                        ) {
                            Text("Hide bottom sheet")
                        }
                    }
                }
            }
        }
    }

    /**
     * 하단 시트를 부분적으로 표시한 다음 사용자가 전체 화면으로 만들거나 닫도록 할 수 있습니다.
     * 이렇게 하려면 skipPartiallyExpanded이 false로 설정된 SheetState 인스턴스를 ModalBottomSheet에 전달합니다.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PartialBottomSheet() {
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { showBottomSheet = true }
            ) {
                Text("Display partial bottom sheet")
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    // 1. modifier에 직접 상태바 패딩을 적용합니다.
                    modifier = Modifier
                        .fillMaxHeight()
                        // 화면 상단 스테이터스 바까지 확장되지 않도롯 설정
                        .statusBarsPadding(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false }
                ) {
                    // 시트 내부 콘텐츠
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("상태바 아래에서 시작되는 바텀시트입니다.")
                    }
                }
            }
        }
    }

    @Composable
    fun ButtonDemo() {
        // [START android_compose_layout_material_button]
        Button(
            onClick = { /* ... */ },
            // Uses ButtonDefaults.ContentPadding by default
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        ) {
            // Inner content including an icon and a text label
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Like")
        }
        // [END android_compose_layout_material_button]
    }

    @Composable
    fun ExtendedFabDemo() {
        // [START android_compose_layout_material_fab]
        ExtendedFloatingActionButton(
            onClick = { /* ... */ },
            icon = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Favorite"
                )
            },
            text = { Text("Like") }
        )
        // [END android_compose_layout_material_fab]
    }

    @Composable
    fun ScaffoldDemo() {
        // [START android_compose_layout_material_scaffold]
        Scaffold(/* ... */) { contentPadding ->
            // Screen content
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
        }
        // [END android_compose_layout_material_scaffold]
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScaffoldTopAppBarDemo() {
        // [START android_compose_layout_material_appbar]
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("My App")
                })
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_appbar]
    }

    @Composable
    fun ScaffoldBottomBarDemo() {
        // [START android_compose_layout_material_bottombar]
        Scaffold(
            bottomBar = {
                BottomAppBar { /* Bottom app bar content */ }
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_bottombar]
    }

    @Composable
    fun ScaffoldFabDemo() {
        // [START android_compose_layout_material_scaffold_fab]
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { /* ... */ }) {
                    /* FAB content */
                }
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_scaffold_fab]
    }

    @Composable
    fun ScaffoldFabPositionDemo() {
        // [START android_compose_layout_material_scaffold_fab_position]
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { /* ... */ }) {
                    /* FAB content */
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_scaffold_fab_position]
    }

    @Composable
    fun ScaffoldFabAndBottomBarDemo() {
        // [START android_compose_layout_material_scaffold_fab_docked]
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { /* ... */ }) {
                    /* FAB content */
                }
            },
            bottomBar = {
                BottomAppBar { /* Bottom app bar content */ }
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_scaffold_fab_docked]
    }

    @Composable
    fun ScaffoldSnackbarDemo() {
        // [START android_compose_layout_material_snackbar]
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show snackbar") },
                    icon = { Icon(Icons.Filled.Image, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Snackbar")
                        }
                    }
                )
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_snackbar]
    }

    @Composable
    fun ScaffoldSnackbarResultDemo() {
        // [START android_compose_layout_material_snackbar_result]
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show snackbar") },
                    icon = { Icon(Icons.Filled.Image, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            val result = snackbarHostState
                                .showSnackbar(
                                    message = "Snackbar",
                                    actionLabel = "Action",
                                    // Defaults to SnackbarDuration.Short
                                    duration = SnackbarDuration.Indefinite
                                )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    /* Handle snackbar action performed */
                                }
                                SnackbarResult.Dismissed -> {
                                    /* Handle snackbar dismissed */
                                }
                            }
                        }
                    }
                )
            }
        ) { contentPadding ->
            // Screen content
            // [START_EXCLUDE silent]
            Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
            // [END_EXCLUDE]
        }
        // [END android_compose_layout_material_snackbar_result]
    }

    @Composable
    fun DrawerDemo() {
        // [START android_compose_layout_material_modal_drawer]
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Text("Drawer title", modifier = Modifier.padding(16.dp))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = "Drawer Item") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    // ...other drawer items
                }
            }
        ) {
            // Screen content
        }
        // [END android_compose_layout_material_modal_drawer]
    }

    @Composable
    fun DrawerGesturesDemo() {
        // [START android_compose_layout_material_modal_drawer_gestures]
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    // Drawer contents
                }
            },
            gesturesEnabled = false
        ) {
            // Screen content
        }
        // [END android_compose_layout_material_modal_drawer_gestures]
    }

    @Composable
    fun DrawerStateDemo() {
        // [START android_compose_layout_material_modal_drawer_programmatic]
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet { /* Drawer content */ }
            },
        ) {
            Scaffold(
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text("Show drawer") },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                        onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    )
                }
            ) { contentPadding ->
                // Screen content
                // [START_EXCLUDE silent]
                Box(modifier = Modifier.padding(contentPadding)) { /* ... */ }
                // [END_EXCLUDE]
            }
        }
        // [END android_compose_layout_material_modal_drawer_programmatic]
    }
}



