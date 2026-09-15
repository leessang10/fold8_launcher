package com.fold8.launcher.ui.taskbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldDarkSurface
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.widgets.AppIconView
import com.fold8.launcher.ui.widgets.AppPairIconView

/**
 * 갤럭시 Z 폴드8 대화면 하단 생산성 태스크바 (Taskbar)
 * - 전체 앱 서랍 토글 버튼
 * - 고정된 즐겨찾기 앱
 * - 최근 사용 앱 (최대 4개)
 * - 등록된 멀티윈도우 앱 페어 바로가기
 */
@Composable
fun FoldTaskbarView(
    pinnedApps: List<AppItem>,
    recentApps: List<AppItem>,
    appPairs: List<AppPairItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onAppPairClick: (AppPairItem) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .wrapContentWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(34.dp)),
        color = FoldDarkCard
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. 전체 앱 서랍 토글 아이콘
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(FoldPrimary.copy(alpha = 0.25f))
            ) {
                Icon(
                    Icons.Default.Apps,
                    contentDescription = "전체 앱 서랍",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 세로 구분선
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.5f)
                    .background(GlassBorder)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 2. 고정 앱 목록 (Pinned Apps)
            pinnedApps.take(6).forEach { app ->
                AppIconView(
                    app = app,
                    iconSize = 44.dp,
                    showLabel = false,
                    onClick = { onAppClick(app) },
                    onSplitLaunch = { onSplitLaunch(app) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // 3. 앱 페어 바로가기 (등록된 경우)
            if (appPairs.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                appPairs.take(2).forEach { pair ->
                    AppPairIconView(
                        pair = pair,
                        iconSize = 44.dp,
                        showLabel = false,
                        onClick = { onAppPairClick(pair) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // 4. 최근 사용한 앱 (Recent Apps)
            if (recentApps.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))

                // 세로 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.5f)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.width(6.dp))

                recentApps.filterNot { recent -> pinnedApps.any { it.id == recent.id } }
                    .take(3)
                    .forEach { recentApp ->
                        AppIconView(
                            app = recentApp,
                            iconSize = 40.dp,
                            showLabel = false,
                            onClick = { onAppClick(recentApp) },
                            onSplitLaunch = { onSplitLaunch(recentApp) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
            }
        }
    }
}
