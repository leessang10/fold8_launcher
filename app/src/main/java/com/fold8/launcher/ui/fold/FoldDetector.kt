package com.fold8.launcher.ui.fold

import android.app.Activity
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.fold8.launcher.domain.model.DeviceFoldState
import com.fold8.launcher.domain.model.DisplayMode
import com.fold8.launcher.domain.model.HingeOrientation
import kotlinx.coroutines.flow.collectLatest

/**
 * 액티비티의 WindowLayoutInfo 스트림을 감지하여
 * 갤럭시 Z 폴드8의 현재 디스플레이 모드(커버/메인/플렉스)와 힌지 영역을 실시간 계산
 */
@Composable
fun rememberFoldState(): DeviceFoldState {
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current

    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    var foldState by remember {
        mutableStateOf(
            DeviceFoldState(
                displayMode = if (screenWidthDp < 600) DisplayMode.COVER else DisplayMode.MAIN_FLAT,
                windowWidthDp = screenWidthDp,
                windowHeightDp = screenHeightDp
            )
        )
    }

    LaunchedEffect(activity, screenWidthDp, screenHeightDp) {
        if (activity == null) return@LaunchedEffect

        WindowInfoTracker.getOrCreate(activity)
            .windowLayoutInfo(activity)
            .collectLatest { layoutInfo ->
                val foldingFeature = layoutInfo.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()

                val isHalfOpened = foldingFeature?.state == FoldingFeature.State.HALF_OPENED
                val isSeparating = foldingFeature?.isSeparating ?: false
                val hingeBounds = foldingFeature?.bounds ?: Rect()

                val orientation = if (foldingFeature?.orientation == FoldingFeature.Orientation.HORIZONTAL) {
                    HingeOrientation.HORIZONTAL
                } else {
                    HingeOrientation.VERTICAL
                }

                val displayMode = when {
                    isHalfOpened -> DisplayMode.MAIN_FLEX
                    screenWidthDp >= 600 -> DisplayMode.MAIN_FLAT
                    else -> DisplayMode.COVER
                }

                foldState = DeviceFoldState(
                    displayMode = displayMode,
                    windowWidthDp = screenWidthDp,
                    windowHeightDp = screenHeightDp,
                    hingeBounds = hingeBounds,
                    hingeOrientation = orientation,
                    isHalfOpened = isHalfOpened,
                    isSeparating = isSeparating
                )
            }
    }

    return foldState
}
