package com.fold8.launcher

import android.graphics.Rect
import com.fold8.launcher.domain.model.AppCategory
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.domain.model.DeviceFoldState
import com.fold8.launcher.domain.model.DisplayMode
import com.fold8.launcher.domain.model.HingeOrientation
import org.junit.Assert.assertEquals
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

    @Test
    fun testAppPairModel() {
        val app1 = AppItem(
            packageName = "com.sec.android.app.sbrowser",
            activityName = "com.sec.android.app.sbrowser.SBrowserMainActivity",
            label = "삼성 인터넷",
            category = AppCategory.TOOLS
        )
        val app2 = AppItem(
            packageName = "com.samsung.android.app.notes",
            activityName = "com.samsung.android.app.notes.memolist.MemoListActivity",
            label = "삼성 노트",
            category = AppCategory.PRODUCTIVITY
        )

        val pair = AppPairItem(
            id = "pair_1",
            title = "인터넷 + 메모",
            primaryApp = app1,
            secondaryApp = app2,
            isVerticalSplit = false
        )

        assertEquals("인터넷 + 메모", pair.title)
        assertEquals("com.sec.android.app.sbrowser", pair.primaryApp.packageName)
        assertEquals("com.samsung.android.app.notes", pair.secondaryApp.packageName)
    }
}
