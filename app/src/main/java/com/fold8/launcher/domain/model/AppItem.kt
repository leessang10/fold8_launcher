package com.fold8.launcher.domain.model

import android.graphics.drawable.Drawable

/**
 * 설치된 개별 애플리케이션 정보
 */
data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val category: AppCategory = AppCategory.OTHERS
) {
    val id: String get() = "$packageName/$activityName"
}

enum class AppCategory(val titleKo: String) {
    ALL("전체"),
    PRODUCTIVITY("생산성"),
    COMMUNICATION("소통/SNS"),
    MEDIA("미디어/엔터"),
    TOOLS("도구"),
    OTHERS("기타")
}

/**
 * 홈 화면 그리드 및 독에 배치되는 아이템
 */
data class HomeItem(
    val id: String,
    val appItem: AppItem? = null,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val isDock: Boolean = false
) {
    val displayName: String get() = appItem?.label ?: ""
}
