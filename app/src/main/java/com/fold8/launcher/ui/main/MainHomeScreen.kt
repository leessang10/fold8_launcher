package com.fold8.launcher.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.ui.taskbar.FoldTaskbarView
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkBackground
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldDarkSurface
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.HingeIndicatorColor
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import com.fold8.launcher.ui.widgets.AppIconView
import com.fold8.launcher.ui.widgets.AppPairIconView
import com.fold8.launcher.ui.widgets.FoldClockWidget

/**
 * 갤럭시 Z 폴드8 내측 대화면 홈 스크린 (Main Inner Screen)
 * - 북-폴드(Book-Fold) 좌/우 2분할 듀얼 페이지 레이아웃
 * - 좌측: 대형 시계 위젯, 생산성 대시보드, 멀티윈도우 앱 페어 그리드
 * - 우측: 5열 고해상도 앱 런치패드 및 빠른 검색
 * - 하단: 폴더블 전용 고정 생산성 태스크바 (Taskbar)
 */
@Composable
fun MainHomeScreen(
    installedApps: List<AppItem>,
    recentApps: List<AppItem>,
    appPairs: List<AppPairItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onAppPairClick: (AppPairItem) -> Unit,
    onCreatePairClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinnedDockApps = installedApps.take(6)
    val rightPaneApps = installedApps.take(20)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 좌우 분할 듀얼 패널 (대화면 최적화)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 84.dp) // 하단 태스크바 영역 확보
        ) {
            // [좌측 패널] 위젯 및 생산성 멀티윈도우 허브
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 대형 디지털 시계 & 폴드 상태 뱃지
                FoldClockWidget(
                    isLargeScreen = true,
                    statusText = "Galaxy Z Fold8"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 멀티윈도우 앱 페어 대시보드 카드
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
                    color = FoldDarkSurface
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.VerticalSplit,
                                    contentDescription = null,
                                    tint = FoldAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "분할 앱 페어 (Split Pairs)",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 페어 생성 버튼
                            IconButton(
                                onClick = onCreatePairClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "페어 추가",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (appPairs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = onCreatePairClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = FoldPrimary.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("자주 쓰는 두 앱을 묶어보세요", fontSize = 12.sp)
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.height(130.dp)
                            ) {
                                items(appPairs) { pair ->
                                    AppPairIconView(
                                        pair = pair,
                                        iconSize = 52.dp,
                                        showLabel = true,
                                        onClick = { onAppPairClick(pair) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 빠른 S펜 / 스마트 액션 카드
                Surface(
                    onClick = onOpenDrawer,
                    shape = RoundedCornerShape(20.dp),
                    color = FoldDarkCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "검색",
                            tint = FoldAccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "앱 검색, 웹 탐색, 멀티태스킹 바로가기...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // 중앙 힌지(Hinge) 심미적 가이드 라인 (은은한 폴더블 세로 그라데이션)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.85f)
                    .align(Alignment.CenterVertically)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                HingeIndicatorColor,
                                Color.Transparent
                            )
                        )
                    )
            )

            // [우측 패널] 5열 고해상도 앱 런치패드
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "자주 사용하는 앱",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "롱프레스: 분할 실행",
                        color = FoldAccentCyan.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rightPaneApps, key = { it.id }) { app ->
                        AppIconView(
                            app = app,
                            iconSize = 58.dp,
                            showLabel = true,
                            onClick = { onAppClick(app) },
                            onSplitLaunch = { onSplitLaunch(app) },
                            onCreatePair = onCreatePairClick
                        )
                    }
                }
            }
        }

        // 하단 플로팅 태스크바 (Taskbar)
        FoldTaskbarView(
            pinnedApps = pinnedDockApps,
            recentApps = recentApps,
            appPairs = appPairs,
            onAppClick = onAppClick,
            onSplitLaunch = onSplitLaunch,
            onAppPairClick = onAppPairClick,
            onOpenDrawer = onOpenDrawer,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}
