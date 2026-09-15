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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldDarkSurface
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.HingeIndicatorColor
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import com.fold8.launcher.ui.widgets.AppIconView
import com.fold8.launcher.ui.widgets.FoldClockWidget

/**
 * 갤럭시 Z 폴드8 대화면 특화 메인 홈 스크린
 * 1. [힌지 주름 회피 스마트 그리드]: 중앙 40dp 주름 영역(Safe Gutter)을 완전 비워두어 아이콘 걸침 방지
 * 4. [커버-메인 북 미러링]: 좌측면(Page 1)과 우측면(Page 2)이 커버 화면의 1, 2페이지와 1:1로 매핑
 * [우측 세로 독]: 서피스 듀오 스타일로 우측 모서리에 세로로 배치되어 오른손 엄지 조작 극대화
 * 5. [텐트 모드]: 상단 거치형 탁상시계 원터치 진입
 */
@Composable
fun MainHomeScreen(
    installedApps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenTentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 북 미러링: Page 1(좌측 16개)과 Page 2(우측 16개) 분할
    val page1Apps = installedApps.take(16)
    val page2Apps = installedApps.drop(16).take(16)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ==========================================
            // [좌측면: Page 1 (북 미러링 1페이지)]
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 12.dp)
            ) {
                // 상단: 텐트 모드 빠른 토글 & 대형 시계
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page 1",
                        color = FoldAccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 텐트/탁상시계 모드 진입 버튼
                    IconButton(
                        onClick = onOpenTentMode,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FoldDarkCard)
                            .border(1.dp, GlassBorder, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.NightlightRound,
                            contentDescription = "탁상시계 모드",
                            tint = FoldAccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 폴드 대형 시계 위젯
                FoldClockWidget(
                    isLargeScreen = true,
                    statusText = "Left Spine"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 좌측 4열 그리드 (Page 1 앱)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(page1Apps, key = { it.id }) { app ->
                        AppIconView(
                            app = app,
                            iconSize = 54.dp,
                            showLabel = true,
                            onClick = { onAppClick(app) },
                            onSplitLaunch = { onSplitLaunch(app) }
                        )
                    }
                }
            }

            // ==========================================
            // [1번: 힌지 주름 회피 안전 여백 (Anti-Crease Safe Gutter)]
            // 중앙 40dp는 물리 주름 보호 구역으로, 어떤 아이콘/글자도 걸치지 않음
            // ==========================================
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // 힌지 위치를 안내하는 1dp의 부드러운 은은한 네온 가이드라인
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.85f)
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
            }

            // ==========================================
            // [우측면: Page 2 (북 미러링 2페이지)]
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page 2",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "롱프레스: 분할 실행",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 빠른 검색바
                Surface(
                    onClick = onOpenDrawer,
                    shape = RoundedCornerShape(18.dp),
                    color = FoldDarkCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
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

                Spacer(modifier = Modifier.height(14.dp))

                // 우측 4열 그리드 (Page 2 앱)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(page2Apps, key = { it.id }) { app ->
                        AppIconView(
                            app = app,
                            iconSize = 54.dp,
                            showLabel = true,
                            onClick = { onAppClick(app) },
                            onSplitLaunch = { onSplitLaunch(app) }
                        )
                    }
                }
            }
        }
    }
}
