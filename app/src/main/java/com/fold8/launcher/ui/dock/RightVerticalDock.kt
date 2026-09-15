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
import androidx.compose.foundation.layout.wrapContentHeight
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
 * 갤럭시 Z 폴드8 특화 우측 세로 슬림 독 (Right Vertical Slim Dock)
 * - 가로/세로, 펼침/접힘 상관없이 항상 화면 우측에 고정되는 슬림형 캡슐 독
 * - 54dp 슬림 폭으로 커버 화면/메인 화면 가리지 않고 오른손 엄지 조작 영역에 위치
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
            .width(54.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(27.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(27.dp)),
        color = FoldDarkCard.copy(alpha = 0.90f)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 상단: 즐겨찾기 주요 앱 세로 정렬 (최대 5개)
            dockApps.take(5).forEach { app ->
                AppIconView(
                    app = app,
                    iconSize = 38.dp,
                    showLabel = false,
                    onClick = { onAppClick(app) },
                    onSplitLaunch = { onSplitLaunch(app) },
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            // 슬림 구분선
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(1.dp)
                    .background(GlassBorder)
            )

            // 최하단 전체 앱 서랍(App Drawer) 진입 버튼
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FoldPrimary.copy(alpha = 0.35f))
                    .border(1.dp, GlassBorder, CircleShape)
            ) {
                Icon(
                    Icons.Default.Apps,
                    contentDescription = "전체 앱 서랍",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
