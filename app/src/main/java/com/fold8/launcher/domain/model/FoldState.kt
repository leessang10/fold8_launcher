package com.fold8.launcher.domain.model

import android.graphics.Rect

/**
 * 갤럭시 Z 폴드8 디바이스의 화면 및 힌지 상태 모델
 */
enum class DisplayMode {
    COVER,      // 외측 커버 디스플레이 (좁은 세로 화면, 한 손 조작 최적화)
    MAIN_FLAT,  // 내측 대화면을 완전히 펼친 상태 (북 스타일 듀얼 페이지/멀티태스킹 태스크바)
    MAIN_FLEX   // 내측 대화면을 반쯤 접은 상태 (플렉스 모드: 상단 뷰/하단 컨트롤러 분할)
}

enum class HingeOrientation {
    VERTICAL,   // 세로 힌지 (좌우로 펼쳐지는 북 스타일 - 갤럭시 폴드 기본)
    HORIZONTAL  // 가로 힌지 (상하 분할 - 기기를 90도 회전한 경우)
}

/**
 * 폴더블 센서 및 WindowManager에서 계산된 디바이스 상태 정보
 */
data class DeviceFoldState(
    val displayMode: DisplayMode = DisplayMode.COVER,
    val windowWidthDp: Int = 360,
    val windowHeightDp: Int = 800,
    val hingeBounds: Rect = Rect(),
    val hingeOrientation: HingeOrientation = HingeOrientation.VERTICAL,
    val isHalfOpened: Boolean = false,
    val isSeparating: Boolean = false
) {
    val isCover: Boolean get() = displayMode == DisplayMode.COVER
    val isMainFlat: Boolean get() = displayMode == DisplayMode.MAIN_FLAT
    val isFlex: Boolean get() = displayMode == DisplayMode.MAIN_FLEX
    val isLargeScreen: Boolean get() = windowWidthDp >= 600
}
