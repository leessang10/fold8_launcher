package com.fold8.launcher.ui.piemenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.widgets.drawableToBitmap
import kotlin.math.cos
import kotlin.math.sin

/**
 * 대화면 폴드8을 양손으로 쥐었을 때,
 * 엄지손가락이 닿는 좌하단/우하단 코너에서 펼쳐지는 부채꼴형(Arc) 퀵 파이 메뉴
 */
@Composable
fun ThumbPieMenu(
    isLeftCorner: Boolean,
    apps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenTrioDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val animScale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pieScale"
    )

    Box(
        modifier = modifier
            .size(240.dp),
        contentAlignment = if (isLeftCorner) Alignment.BottomStart else Alignment.BottomEnd
    ) {
        // 1. 펼쳐진 아크(부채꼴) 메뉴 아이템들
        AnimatedVisibility(
            visible = isExpanded,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(animScale)
            ) {
                if (isLeftCorner) {
                    // 좌측 코너: 시스템 생산성 액션들 (각도 0도 ~ 90도 부채꼴)
                    val leftActions = listOf(
                        PieAction("홈", Icons.Default.Home) { isExpanded = false },
                        PieAction("앱 서랍", Icons.Default.Apps) { isExpanded = false; onOpenDrawer() },
                        PieAction("3분할", Icons.Default.ViewCarousel) { isExpanded = false; onOpenTrioDialog() },
                        PieAction("분할", Icons.Default.VerticalSplit) { isExpanded = false; onOpenDrawer() },
                        PieAction("검색", Icons.Default.Search) { isExpanded = false; onOpenDrawer() }
                    )

                    leftActions.forEachIndexed { index, action ->
                        // 0도(우측)부터 90도(상단) 사이 5개 분할
                        val angleDeg = 15.0 + (index * 17.5)
                        val angleRad = Math.toRadians(angleDeg)
                        val radius = 135.0 // dp
                        val offsetX = (radius * cos(angleRad)).toInt()
                        val offsetY = -(radius * sin(angleRad)).toInt()

                        PieActionButton(
                            label = action.label,
                            icon = action.icon,
                            onClick = action.onClick,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset { IntOffset(offsetX.dp.roundToPx(), offsetY.dp.roundToPx()) }
                        )
                    }
                } else {
                    // 우측 코너: 빠른 앱 즐겨찾기 5개 (각도 90도 ~ 180도 부채꼴)
                    val favoriteApps = apps.take(5)
                    favoriteApps.forEachIndexed { index, app ->
                        val angleDeg = 15.0 + (index * 17.5)
                        val angleRad = Math.toRadians(angleDeg)
                        val radius = 135.0 // dp
                        val offsetX = -(radius * cos(angleRad)).toInt()
                        val offsetY = -(radius * sin(angleRad)).toInt()

                        PieAppButton(
                            app = app,
                            onClick = {
                                isExpanded = false
                                onAppClick(app)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset { IntOffset(offsetX.dp.roundToPx(), offsetY.dp.roundToPx()) }
                        )
                    }
                }
            }
        }

        // 2. 코너 트리거 버튼 (Hot-spot Trigger Button)
        Box(
            modifier = Modifier
                .padding(12.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FoldPrimary.copy(alpha = 0.85f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(1.5.dp, if (isExpanded) FoldAccentCyan else GlassBorder, CircleShape)
                .clickable {
                    isExpanded = !isExpanded
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.FlashOn,
                contentDescription = "엄지 퀵 메뉴",
                tint = if (isExpanded) Color.White else FoldAccentCyan,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private data class PieAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun PieActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(FoldDarkCard)
            .border(1.dp, FoldAccentCyan.copy(alpha = 0.6f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = FoldAccentCyan,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun PieAppButton(
    app: AppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(app.id) { drawableToBitmap(app.icon) }

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(FoldDarkCard)
            .border(1.dp, FoldPrimary.copy(alpha = 0.8f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = app.label.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
