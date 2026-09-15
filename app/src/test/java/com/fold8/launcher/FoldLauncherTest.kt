package com.fold8.launcher

import android.graphics.Rect
import com.fold8.launcher.domain.model.DeviceFoldState
import com.fold8.launcher.domain.model.DisplayMode
import com.fold8.launcher.domain.model.HingeOrientation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoldLauncherTest {

    @Test
    fun testCoverScreenState() {
        val coverState = DeviceFoldState(
            displayMode = DisplayMode.COVER,
            windowWidthDp = 380,
            windowHeightDp = 840,
            isHalfOpened = false
        )
        assertTrue(coverState.isCover)
        assertFalse(coverState.isLargeScreen)
        assertFalse(coverState.isFlex)
    }

    @Test
    fun testMainFlatScreenState() {
        val mainFlatState = DeviceFoldState(
            displayMode = DisplayMode.MAIN_FLAT,
            windowWidthDp = 760,
            windowHeightDp = 880,
            isHalfOpened = false
        )
        assertTrue(mainFlatState.isMainFlat)
        assertTrue(mainFlatState.isLargeScreen)
        assertFalse(mainFlatState.isCover)
    }

    @Test
    fun testMainFlexModeState() {
        val flexState = DeviceFoldState(
            displayMode = DisplayMode.MAIN_FLEX,
            windowWidthDp = 760,
            windowHeightDp = 880,
            hingeBounds = Rect(0, 438, 760, 442),
            hingeOrientation = HingeOrientation.HORIZONTAL,
            isHalfOpened = true,
            isSeparating = true
        )
        assertTrue(flexState.isFlex)
        assertTrue(flexState.isHalfOpened)
        assertTrue(flexState.isSeparating)
    }
}
