package com.fold8.launcher.ui.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkBackground
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import com.fold8.launcher.ui.widgets.AppIconView
import com.fold8.launcher.ui.widgets.AppPairIconView
import com.fold8.launcher.ui.widgets.FoldClockWidget

/**
 * 갤럭시 Z 폴드8 외측 커버 화면 (Cover Screen)
 * - 슬림한 세로 비율에 맞춘 4열 한 손 조작 레이아웃
 * - 상단 글랜스 시계 위젯 및 신속 검색바
 * - 하단 퀵 독(Quick Dock)
 */
@Composable
fun CoverHomeScreen(
    installedApps: List<AppItem>,
    appPairs: List<AppPairItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onAppPairClick: (AppPairItem) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinnedApps = installedApps.take(16)
    val dockApps = installedApps.take(4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. 상단 컴팩트 시계 위젯
            FoldClockWidget(
                isLargeScreen = false,
                statusText = "Cover Display"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. 빠른 검색바 (탭 시 앱 서랍 열림)
            Surface(
                onClick = onOpenDrawer,
                shape = RoundedCornerShape(22.dp),
                color = FoldDarkCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
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
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        "앱 및 웹 검색...",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // 3. 앱 페어가 등록되어 있는 경우 가로 칩 스트립 표시
            if (appPairs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(appPairs) { pair ->
                        AppPairIconView(
                            pair = pair,
                            iconSize = 48.dp,
                            showLabel = true,
                            onClick = { onAppPairClick(pair) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. 메인 홈 그리드 (4열)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(pinnedApps, key = { it.id }) { app ->
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

        // 5. 하단 퀵 독 (Bottom Dock)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(72.dp)
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
                        iconSize = 48.dp,
                        showLabel = false,
                        onClick = { onAppClick(app) },
                        onSplitLaunch = { onSplitLaunch(app) }
                    )
                }

                // 앱 서랍 진입 버튼
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(FoldPrimary.copy(alpha = 0.25f))
                ) {
                    Icon(
                        Icons.Default.Apps,
                        contentDescription = "전체 앱",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
