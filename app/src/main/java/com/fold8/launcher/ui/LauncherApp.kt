package com.fold8.launcher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fold8.launcher.domain.model.DisplayMode
import com.fold8.launcher.ui.appdrawer.AppDrawerSheet
import com.fold8.launcher.ui.cover.CoverHomeScreen
import com.fold8.launcher.ui.flex.FlexHomeScreen
import com.fold8.launcher.ui.fold.rememberFoldState
import com.fold8.launcher.ui.main.MainHomeScreen
import com.fold8.launcher.ui.tent.TentClockScreen
import com.fold8.launcher.ui.theme.Fold8LauncherTheme
import com.fold8.launcher.ui.widgets.CreateAppPairDialog
import com.fold8.launcher.ui.widgets.CreateAppTrioDialog

/**
 * 갤럭시 Z 폴드8 런처 메인 컴포저블
 * - 화면 연속성(App Continuity): 커버/메인/플렉스 전환 시 매끄러운 트랜지션
 * - 4대 폴드 특화 기능 코디네이터:
 *   1. 힌지 주름 회피 스마트 그리드
 *   4. 커버-메인 북 미러링
 *   5. 텐트 모드 / 거치형 AOD 탁상시계
 *   우측 세로 독(Right Vertical Dock)
 */
@Composable
fun LauncherApp(
    viewModel: LauncherViewModel = viewModel()
) {
    val foldState = rememberFoldState()

    val installedApps by viewModel.installedApps.collectAsState()
    val recentApps by viewModel.recentApps.collectAsState()
    val appPairs by viewModel.appPairs.collectAsState()
    val appTrios by viewModel.appTrios.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isAppDrawerOpen by viewModel.isAppDrawerOpen.collectAsState()
    val isCreatePairDialogOpen by viewModel.isCreatePairDialogOpen.collectAsState()
    val isCreateTrioDialogOpen by viewModel.isCreateTrioDialogOpen.collectAsState()

    // 5번 기능: 텐트 모드(탁상시계) 활성화 상태
    var isTentModeActive by remember { mutableStateOf(false) }

    // 뒤로가기 제스처 처리 (텐트 모드나 앱 서랍 열려있을 경우 닫기)
    BackHandler(enabled = isTentModeActive || isAppDrawerOpen) {
        if (isTentModeActive) {
            isTentModeActive = false
        } else if (isAppDrawerOpen) {
            viewModel.setAppDrawerOpen(false)
        }
    }

    Fold8LauncherTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // 배경 월페이퍼가 투명하게 투과되도록 깊이감 있는 은은한 그라데이션 오버레이 적용
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66070A12),
                            Color(0x99090D18),
                            Color(0xCC050810)
                        )
                    )
                )
        ) {
            // 폴드8 디스플레이 모드에 따른 화면 전환
            AnimatedContent(
                targetState = foldState.displayMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "FoldScreenTransition"
            ) { mode ->
                when (mode) {
                    DisplayMode.COVER -> {
                        CoverHomeScreen(
                            installedApps = installedApps,
                            onAppClick = { viewModel.launchApp(it) },
                            onSplitLaunch = { viewModel.launchAppAdjacent(it) },
                            onOpenDrawer = { viewModel.setAppDrawerOpen(true) },
                            onOpenTentMode = { isTentModeActive = true }
                        )
                    }
                    DisplayMode.MAIN_FLAT -> {
                        MainHomeScreen(
                            installedApps = installedApps,
                            onAppClick = { viewModel.launchApp(it) },
                            onSplitLaunch = { viewModel.launchAppAdjacent(it) },
                            onOpenDrawer = { viewModel.setAppDrawerOpen(true) },
                            onOpenTentMode = { isTentModeActive = true }
                        )
                    }
                    DisplayMode.MAIN_FLEX -> {
                        FlexHomeScreen(
                            foldState = foldState,
                            installedApps = installedApps,
                            appPairs = appPairs,
                            onAppClick = { viewModel.launchApp(it) },
                            onSplitLaunch = { viewModel.launchAppAdjacent(it) },
                            onOpenDrawer = { viewModel.setAppDrawerOpen(true) }
                        )
                    }
                }
            }

            // 5번 기능: 텐트 모드 / 탁상시계 전체화면 오버레이
            AnimatedVisibility(
                visible = isTentModeActive,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(250))
            ) {
                TentClockScreen(
                    onExit = { isTentModeActive = false }
                )
            }

            // 슬라이드업 전체 앱 서랍 (App Drawer)
            AnimatedVisibility(
                visible = isAppDrawerOpen,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(280)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(240)
                ) + fadeOut()
            ) {
                AppDrawerSheet(
                    apps = filteredApps,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    isLargeScreen = foldState.isLargeScreen,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onCategorySelect = { viewModel.selectCategory(it) },
                    onAppClick = { app ->
                        viewModel.launchApp(app)
                        viewModel.setAppDrawerOpen(false)
                    },
                    onSplitLaunch = { app ->
                        viewModel.launchAppAdjacent(app)
                        viewModel.setAppDrawerOpen(false)
                    },
                    onCreatePairClick = {
                        viewModel.setCreatePairDialogOpen(true)
                    },
                    onClose = {
                        viewModel.setAppDrawerOpen(false)
                    }
                )
            }

            // 2분할 앱 페어 신규 생성 다이얼로그
            if (isCreatePairDialogOpen) {
                CreateAppPairDialog(
                    availableApps = installedApps,
                    onDismiss = { viewModel.setCreatePairDialogOpen(false) },
                    onSave = { title, first, second ->
                        viewModel.createAndSavePair(title, first, second)
                    }
                )
            }

            // 3분할 앱 트리오 신규 생성 다이얼로그
            if (isCreateTrioDialogOpen) {
                CreateAppTrioDialog(
                    availableApps = installedApps,
                    onDismiss = { viewModel.setCreateTrioDialogOpen(false) },
                    onSave = { title, first, second, third ->
                        viewModel.createAndSaveTrio(title, first, second, third)
                    }
                )
            }
        }
    }
}
