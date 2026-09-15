package com.fold8.launcher.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
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
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.widgets.AppIconView

/**
 * 갤럭시 Z 폴드8 대화면 특화 우측 세로 독 (Right Vertical Dock)
 * - 서피스 듀오 / 폴더블 태블릿 스타일로 화면 우측 끝단에 세로로 배치
 * - 오른손으로 기기를 쥔 상태에서 엄지손가락 하나로 모든 주요 앱과 앱 서랍을 실행
 */
@Composable
fun RightVerticalDock(
    dockApps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(68.dp)
            .fillMaxHeight(0.82f)
            .clip(RoundedCornerShape(34.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(34.dp)),
        color = FoldDarkCard
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단~중단: 고정 즐겨찾기 앱 세로 정렬 (최대 5개)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                dockApps.take(5).forEach { app ->
                    AppIconView(
                        app = app,
                        iconSize = 46.dp,
                        showLabel = false,
                        onClick = { onAppClick(app) },
                        onSplitLaunch = { onSplitLaunch(app) },
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            // 구분선 및 최하단 전체 앱 서랍(App Drawer) 토글 버튼
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(1.dp)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.height(12.dp))

                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(FoldPrimary.copy(alpha = 0.35f))
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Apps,
                        contentDescription = "전체 앱 서랍",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
