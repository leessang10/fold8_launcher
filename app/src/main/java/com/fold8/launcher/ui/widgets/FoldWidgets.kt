package com.fold8.launcher.ui.widgets

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkSurface
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 안드로이드 Drawable을 Compose용 ImageBitmap으로 안전하게 변환
 */
fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

/**
 * 개별 앱 아이콘 뷰 (탭 실행, 롱프레스 시 화면 분할 / 페어 추가 메뉴 제공)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconView(
    app: AppItem,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onSplitLaunch: (() -> Unit)? = null,
    onCreatePair: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.90f else 1.0f, label = "scale")

    val bitmap = remember(app.id) { drawableToBitmap(app.icon) }

    Column(
        modifier = modifier
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = {
                    if (onSplitLaunch != null || onCreatePair != null) {
                        showMenu = true
                    }
                }
            )
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(FoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (iconSize.value * 0.4f).sp
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = TextPrimary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color(0xFF1E2433))
        ) {
            DropdownMenuItem(
                text = { Text("실행", color = TextPrimary) },
                onClick = {
                    showMenu = false
                    onClick()
                }
            )
            if (onSplitLaunch != null) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Default.VerticalSplit, contentDescription = null, tint = FoldAccentCyan)
                    },
                    text = { Text("화면 분할(Split)로 열기", color = FoldAccentCyan) },
                    onClick = {
                        showMenu = false
                        onSplitLaunch()
                    }
                )
            }
            if (onCreatePair != null) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    },
                    text = { Text("앱 페어 생성에 사용", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onCreatePair()
                    }
                )
            }
        }
    }
}

/**
 * 대화면 멀티윈도우 전용 앱 페어(App Pair) 아이콘 뷰
 * 두 개의 앱 아이콘이 사선/겹침 형태로 융합되어 표시됨
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppPairIconView(
    pair: AppPairItem,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    val firstBitmap = remember(pair.primaryApp.id) { drawableToBitmap(pair.primaryApp.icon) }
    val secondBitmap = remember(pair.secondaryApp.id) { drawableToBitmap(pair.secondaryApp.icon) }

    Column(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = { if (onDelete != null) showMenu = true }
            )
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(FoldPrimary.copy(alpha = 0.4f), FoldAccentCyan.copy(alpha = 0.3f))
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            // 첫 번째 앱 (좌상단 배치)
            if (firstBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = firstBitmap.asImageBitmap(),
                    contentDescription = pair.primaryApp.label,
                    modifier = Modifier
                        .size(iconSize * 0.58f)
                        .offset(x = -(iconSize * 0.16f), y = -(iconSize * 0.16f))
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            // 두 번째 앱 (우하단 배치)
            if (secondBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = secondBitmap.asImageBitmap(),
                    contentDescription = pair.secondaryApp.label,
                    modifier = Modifier
                        .size(iconSize * 0.58f)
                        .offset(x = iconSize * 0.16f, y = iconSize * 0.16f)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            // 앱 페어 분할 표시 뱃지
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(FoldAccentCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.VerticalSplit,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pair.title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color(0xFF1E2433))
        ) {
            DropdownMenuItem(
                text = { Text("멀티윈도우 분할 실행", color = FoldAccentCyan) },
                onClick = {
                    showMenu = false
                    onClick()
                }
            )
            if (onDelete != null) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                    },
                    text = { Text("페어 삭제", color = Color.Red) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

/**
 * 폴드 전용 스마트 시계 & 상태 위젯
 */
@Composable
fun FoldClockWidget(
    modifier: Modifier = Modifier,
    isLargeScreen: Boolean = false,
    statusText: String? = null
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("M월 d일 EEEE", Locale.KOREAN)
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
        color = FoldDarkSurface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = if (isLargeScreen) Alignment.Start else Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentDate,
                    color = TextSecondary,
                    fontSize = if (isLargeScreen) 16.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (statusText != null) {
                    Box(
                        modifier = Modifier
                            .background(FoldAccentCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.dp, FoldAccentCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = FoldAccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentTime,
                color = TextPrimary,
                fontSize = if (isLargeScreen) 64.sp else 46.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
        }
    }
}

/**
 * 새로운 앱 페어(App Pair) 생성 다이얼로그
 */
@Composable
fun CreateAppPairDialog(
    availableApps: List<AppItem>,
    onDismiss: () -> Unit,
    onSave: (title: String, first: AppItem, second: AppItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedFirst by remember { mutableStateOf<AppItem?>(null) }
    var selectedSecond by remember { mutableStateOf<AppItem?>(null) }
    var step by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (step == 1) "첫 번째 분할 앱 선택" else if (step == 2) "두 번째 분할 앱 선택" else "앱 페어 이름 지정",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.height(360.dp)) {
                if (step == 1 || step == 2) {
                    LazyColumn {
                        items(availableApps) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (step == 1) {
                                            selectedFirst = app
                                            step = 2
                                        } else {
                                            selectedSecond = app
                                            title = "${selectedFirst?.label} + ${app.label}"
                                            step = 3
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val bmp = remember(app.id) { drawableToBitmap(app.icon) }
                                if (bmp != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(app.label, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "지정한 두 앱이 폴드 대화면에서 동시에 분할 실행됩니다.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("앱 페어 이름") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (step == 3) {
                Button(
                    onClick = {
                        val first = selectedFirst
                        val second = selectedSecond
                        if (first != null && second != null) {
                            onSave(title, first, second)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoldPrimary)
                ) {
                    Text("생성")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSecondary)
            }
        },
        containerColor = Color(0xFF1B202E)
    )
}
