package com.fold8.launcher.ui.cover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import com.fold8.launcher.ui.widgets.AppIconView
import com.fold8.launcher.ui.widgets.FoldClockWidget

/**
 * 갤럭시 Z 폴드8 외측 커버 화면 (Cover Screen)
 * 4. [북 미러링]: HorizontalPager를 통해 Page 1과 Page 2를 좌우로 스와이프
 *    (기기를 펼치면 이 두 페이지가 대화면 좌/우로 한눈에 결합됨)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoverHomeScreen(
    installedApps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenTentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 북 미러링 데이터: Page 1(좌측)과 Page 2(우측)
    val page1Apps = installedApps.take(16)
    val page2Apps = installedApps.drop(16).take(16)
    val dockApps = installedApps.take(4)

    val pagerState = rememberPagerState(pageCount = { 2 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 상단: 텐트 모드 버튼 & 페이지 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pagerState.currentPage == 0) "Cover • Page 1" else "Cover • Page 2",
                    color = FoldAccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onOpenTentMode,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(FoldDarkCard)
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        Icons.Default.NightlightRound,
                        contentDescription = "탁상시계 모드",
                        tint = FoldAccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 1. 상단 컴팩트 시계 위젯
            FoldClockWidget(
                isLargeScreen = false,
                statusText = "Book Fold Mirror"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 빠른 검색바
            Surface(
                onClick = onOpenDrawer,
                shape = RoundedCornerShape(20.dp),
                color = FoldDarkCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "검색",
                        tint = FoldAccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "앱 검색...",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. 북 미러링 Pager (Page 1 ⟷ Page 2 좌우 스와이프)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val currentApps = if (page == 0) page1Apps else page2Apps
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentApps, key = { it.id }) { app ->
                        AppIconView(
                            app = app,
                            iconSize = 52.dp,
                            showLabel = true,
                            onClick = { onAppClick(app) },
                            onSplitLaunch = { onSplitLaunch(app) }
                        )
                    }
                }
            }
        }

        // 페이지 인디케이터 (Page 1: ● ○ / Page 2: ○ ●)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 86.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(2) { pageIndex ->
                val isSelected = pagerState.currentPage == pageIndex
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) FoldAccentCyan else Color(0x66FFFFFF))
                )
            }
        }

        // 4. 하단 퀵 독 (Bottom Quick Dock)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(32.dp)),
            color = FoldDarkCard
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dockApps.forEach { app ->
                    AppIconView(
                        app = app,
                        iconSize = 46.dp,
                        showLabel = false,
                        onClick = { onAppClick(app) },
                        onSplitLaunch = { onSplitLaunch(app) }
                    )
                }

                // 앱 서랍 진입 버튼
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FoldPrimary.copy(alpha = 0.25f))
                ) {
                    Icon(
                        Icons.Default.Apps,
                        contentDescription = "전체 앱",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
