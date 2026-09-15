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
 * 대화면 폴드 멀티태스킹을 위한 앱 페어 (App Pair: 2개 앱 동시 분할 실행)
 */
data class AppPairItem(
    val id: String,
    val title: String,
    val primaryApp: AppItem,
    val secondaryApp: AppItem,
    val isVerticalSplit: Boolean = false
)

enum class TrioLayoutType(val titleKo: String) {
    ONE_LARGE_TWO_SMALL("주 화면 1개 + 보조 2개"),
    THREE_COLUMNS("3열 균등 분할")
}

/**
 * 대화면 폴드8 특화 앱 트리오 (App Trio: 3개 앱 동시 3분할 실행)
 */
data class AppTrioItem(
    val id: String,
    val title: String,
    val primaryApp: AppItem,
    val secondaryApp: AppItem,
    val tertiaryApp: AppItem,
    val layoutType: TrioLayoutType = TrioLayoutType.ONE_LARGE_TWO_SMALL
)

/**
 * 홈 화면 그리드 및 독에 배치되는 아이템
 */
data class HomeItem(
    val id: String,
    val appItem: AppItem? = null,
    val appPairItem: AppPairItem? = null,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val isDock: Boolean = false
) {
    val isAppPair: Boolean get() = appPairItem != null
    val displayName: String get() = appPairItem?.title ?: appItem?.label ?: ""
}
