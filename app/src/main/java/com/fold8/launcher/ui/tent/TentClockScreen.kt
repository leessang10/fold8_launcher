package com.fold8.launcher.ui.tent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 갤럭시 Z 폴드8 텐트 모드 / 거치형 AOD 탁상시계 (Tent & Desk Stand Mode)
 * - 기기를 삼각형(Tent)으로 세우거나 90도 거치했을 때 활성화
 * - 번인 방지 미니멀 AMOLED 블랙 디자인 및 대형 디지털 시계
 */
@Composable
fun TentClockScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentSeconds by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val secFmt = SimpleDateFormat("ss", Locale.getDefault())
        val dateFmt = SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREAN)
        while (true) {
            val now = Date()
            currentTime = timeFmt.format(now)
            currentSeconds = secFmt.format(now)
            currentDate = dateFmt.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04060A))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExit
            )
            .padding(28.dp)
    ) {
        // 상단: 텐트 모드 상태 뱃지 & 닫기 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(FoldAccentCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.NightlightRound,
                        contentDescription = null,
                        tint = FoldAccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tent Stand Mode",
                    color = FoldAccentCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onExit,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "종료",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 중앙: 대형 글랜스 시계
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentDate,
                color = TextSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = currentTime,
                    color = TextPrimary,
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentSeconds,
                    color = FoldAccentCyan,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x1AFFFFFF),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "스탠드 거치 중 • 탭하여 홈으로 복귀",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
