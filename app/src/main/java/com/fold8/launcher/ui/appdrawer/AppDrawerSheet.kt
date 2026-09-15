package com.fold8.launcher.ui.appdrawer

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.fold8.launcher.domain.model.AppCategory
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.ui.theme.FoldAccentCyan
import com.fold8.launcher.ui.theme.FoldDarkBackground
import com.fold8.launcher.ui.theme.FoldDarkCard
import com.fold8.launcher.ui.theme.FoldPrimary
import com.fold8.launcher.ui.theme.GlassBorder
import com.fold8.launcher.ui.theme.TextPrimary
import com.fold8.launcher.ui.theme.TextSecondary
import com.fold8.launcher.ui.widgets.AppIconView

/**
 * 전체 앱 서랍 (App Drawer)
 * 폴더블 화면 폭에 맞추어 4열(커버) 또는 6열(메인 대화면)으로 자동 전환
 */
@Composable
fun AppDrawerSheet(
    apps: List<AppItem>,
    searchQuery: String,
    selectedCategory: AppCategory,
    isLargeScreen: Boolean,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (AppCategory) -> Unit,
    onAppClick: (AppItem) -> Unit,
    onSplitLaunch: (AppItem) -> Unit,
    onCreatePairClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val columnCount = if (isLargeScreen) 6 else 4

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(FoldDarkBackground.copy(alpha = 0.95f)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = if (isLargeScreen) 32.dp else 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 상단 검색바 & 앱 페어 생성 액션
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "앱 검색...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = FoldAccentCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "지우기",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FoldPrimary,
                        unfocusedBorderColor = GlassBorder,
                        focusedContainerColor = FoldDarkCard,
                        unfocusedContainerColor = FoldDarkCard,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 앱 페어 생성 버튼
                IconButton(
                    onClick = onCreatePairClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(FoldDarkCard)
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        Icons.Default.GroupAdd,
                        contentDescription = "앱 페어 생성",
                        tint = FoldAccentCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 닫기 버튼
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(FoldDarkCard)
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 카테고리 필터 칩 바
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AppCategory.values()) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = category.titleKo,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FoldPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = FoldDarkCard,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) FoldAccentCyan else GlassBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 앱 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps, key = { it.id }) { app ->
                    AppIconView(
                        app = app,
                        iconSize = if (isLargeScreen) 62.dp else 52.dp,
                        showLabel = true,
                        onClick = { onAppClick(app) },
                        onSplitLaunch = { onSplitLaunch(app) },
                        onCreatePair = onCreatePairClick
                    )
                }
            }
        }
    }
}
