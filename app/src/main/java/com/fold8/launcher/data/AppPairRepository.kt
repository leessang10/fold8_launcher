package com.fold8.launcher.data

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.domain.model.AppTrioItem
import com.fold8.launcher.domain.model.TrioLayoutType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 대화면 폴드8을 위한 멀티태스킹 앱 페어(App Pair) 및 앱 트리오(App Trio) 관리 저장소
 */
class AppPairRepository(
    private val context: Context,
    private val appRepository: AppRepository
) {
    private val _appPairs = MutableStateFlow<List<AppPairItem>>(emptyList())
    val appPairs: StateFlow<List<AppPairItem>> = _appPairs.asStateFlow()

    private val _appTrios = MutableStateFlow<List<AppTrioItem>>(emptyList())
    val appTrios: StateFlow<List<AppTrioItem>> = _appTrios.asStateFlow()

    init {
        loadDefaultPairs()
    }

    private fun loadDefaultPairs() {
        val allApps = appRepository.installedApps.value
        if (allApps.isEmpty()) {
            return
        }

        val browser = allApps.firstOrNull { it.category == com.fold8.launcher.domain.model.AppCategory.TOOLS }
            ?: allApps.firstOrNull()
        val notes = allApps.firstOrNull { it.category == com.fold8.launcher.domain.model.AppCategory.PRODUCTIVITY }
            ?: allApps.getOrNull(1)
        val media = allApps.firstOrNull { it.category == com.fold8.launcher.domain.model.AppCategory.MEDIA }
            ?: allApps.getOrNull(2)

        val pairs = mutableListOf<AppPairItem>()
        if (browser != null && notes != null && browser.packageName != notes.packageName) {
            pairs.add(
                AppPairItem(
                    id = "pair_prod",
                    title = "웹서핑 + 메모",
                    primaryApp = browser,
                    secondaryApp = notes,
                    isVerticalSplit = false
                )
            )
        }
        _appPairs.value = pairs

        // 기본 3분할 앱 트리오 등록
        val trios = mutableListOf<AppTrioItem>()
        if (browser != null && notes != null && media != null) {
            trios.add(
                AppTrioItem(
                    id = "trio_prod",
                    title = "인터넷 + 메모 + 미디어",
                    primaryApp = browser,
                    secondaryApp = notes,
                    tertiaryApp = media,
                    layoutType = TrioLayoutType.ONE_LARGE_TWO_SMALL
                )
            )
        }
        _appTrios.value = trios
    }

    fun addAppPair(title: String, first: AppItem, second: AppItem, isVertical: Boolean = false) {
        val newPair = AppPairItem(
            id = "pair_${System.currentTimeMillis()}",
            title = title.ifBlank { "${first.label} + ${second.label}" },
            primaryApp = first,
            secondaryApp = second,
            isVerticalSplit = isVertical
        )
        _appPairs.value = _appPairs.value + newPair
    }

    fun removeAppPair(pairId: String) {
        _appPairs.value = _appPairs.value.filterNot { it.id == pairId }
    }

    fun addAppTrio(title: String, first: AppItem, second: AppItem, third: AppItem, layout: TrioLayoutType = TrioLayoutType.ONE_LARGE_TWO_SMALL) {
        val newTrio = AppTrioItem(
            id = "trio_${System.currentTimeMillis()}",
            title = title.ifBlank { "${first.label} + ${second.label} + ${third.label}" },
            primaryApp = first,
            secondaryApp = second,
            tertiaryApp = third,
            layoutType = layout
        )
        _appTrios.value = _appTrios.value + newTrio
    }

    fun removeAppTrio(trioId: String) {
        _appTrios.value = _appTrios.value.filterNot { it.id == trioId }
    }

    /**
     * 폴더블 멀티윈도우로 두 개의 앱을 나란히 분할 실행
     */
    fun launchAppPair(pair: AppPairItem): Boolean {
        return try {
            val pm = context.packageManager

            // 1. 첫 번째 주 앱을 기본 창으로 실행
            val intent1 = pm.getLaunchIntentForPackage(pair.primaryApp.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            if (intent1 != null) {
                context.startActivity(intent1)
            }

            // 2. 약간의 딜레이(150ms) 후 두 번째 앱을 ADJACENT(인접 분할 창)으로 실행
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val intent2 = pm.getLaunchIntentForPackage(pair.secondaryApp.packageName)?.apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                    }
                    if (intent2 != null) {
                        context.startActivity(intent2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 150)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 갤럭시 Z 폴드8 대화면 특화 3분할(App Trio) 동시 실행
     */
    fun launchAppTrio(trio: AppTrioItem): Boolean {
        return try {
            val pm = context.packageManager

            // 1. 주 앱(Primary) 실행 (화면 50% 분할 메인)
            val intent1 = pm.getLaunchIntentForPackage(trio.primaryApp.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            if (intent1 != null) {
                context.startActivity(intent1)
            }

            val handler = Handler(Looper.getMainLooper())

            // 2. 보조 1 앱(Secondary) 실행
            handler.postDelayed({
                try {
                    val intent2 = pm.getLaunchIntentForPackage(trio.secondaryApp.packageName)?.apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                    }
                    if (intent2 != null) {
                        context.startActivity(intent2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 160)

            // 3. 보조 2 앱(Tertiary) 실행 (남은 분할 창으로 진입)
            handler.postDelayed({
                try {
                    val intent3 = pm.getLaunchIntentForPackage(trio.tertiaryApp.packageName)?.apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                    }
                    if (intent3 != null) {
                        context.startActivity(intent3)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 320)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
