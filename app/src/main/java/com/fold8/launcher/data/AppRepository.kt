package com.fold8.launcher.data

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle
import com.fold8.launcher.domain.model.AppCategory
import com.fold8.launcher.domain.model.AppItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 기기에 설치된 앱 목록을 조회, 캐싱하고
 * 단일 앱 및 멀티윈도우(Split Screen) 실행을 관장하는 저장소
 */
class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val launcherApps: LauncherApps? = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshApps()
        }
    }

    init {
        registerPackageReceiver()
        refreshApps()
    }

    private fun registerPackageReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(packageReceiver, filter)
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(packageReceiver)
        } catch (_: Exception) {}
    }

    fun refreshApps() {
        repositoryScope.launch {
            val apps = loadInstalledApps()
            _installedApps.value = apps
        }
    }

    private suspend fun loadInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val appList = mutableListOf<AppItem>()
        val selfPackage = context.packageName

        if (launcherApps != null) {
            val profiles = launcherApps.profiles
            for (profile in profiles) {
                val activityList = launcherApps.getActivityList(null, profile)
                for (activity in activityList) {
                    val pkgName = activity.applicationInfo.packageName
                    if (pkgName == selfPackage) continue

                    val label = activity.label.toString()
                    val icon = activity.getIcon(context.resources.displayMetrics.densityDpi)
                    val isSystem = (activity.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    appList.add(
                        AppItem(
                            packageName = pkgName,
                            activityName = activity.name,
                            label = label,
                            icon = icon,
                            isSystemApp = isSystem,
                            category = categorizeApp(pkgName, label)
                        )
                    )
                }
            }
        } else {
            // 폴백: 표준 Intent 쿼리
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
            for (info in resolveInfos) {
                val pkgName = info.activityInfo.packageName
                if (pkgName == selfPackage) continue

                val label = info.loadLabel(packageManager).toString()
                val icon = info.loadIcon(packageManager)
                val isSystem = (info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                appList.add(
                    AppItem(
                        packageName = pkgName,
                        activityName = info.activityInfo.name,
                        label = label,
                        icon = icon,
                        isSystemApp = isSystem,
                        category = categorizeApp(pkgName, label)
                    )
                )
            }
        }

        appList.sortedBy { it.label.lowercase() }
    }

    /**
     * 앱 실행
     */
    fun launchApp(app: AppItem): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
            if (intent != null) {
                context.startActivity(intent)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 폴더블 대화면 분할 화면(Split Screen)으로 앱 실행
     * (FLAG_ACTIVITY_LAUNCH_ADJACENT 플래그를 사용하여 대화면의 반대쪽 창에서 실행)
     */
    fun launchAppAdjacent(app: AppItem): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
            }
            if (intent != null) {
                context.startActivity(intent)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 앱 카테고리 자동 판별
     */
    private fun categorizeApp(pkg: String, label: String): AppCategory {
        val lower = "$pkg $label".lowercase()
        return when {
            lower.contains("message") || lower.contains("kakao") || lower.contains("chat") ||
            lower.contains("tele") || lower.contains("call") || lower.contains("phone") ||
            lower.contains("contact") || lower.contains("mail") || lower.contains("instagram") -> AppCategory.COMMUNICATION

            lower.contains("note") || lower.contains("doc") || lower.contains("sheet") ||
            lower.contains("office") || lower.contains("calendar") || lower.contains("drive") ||
            lower.contains("pdf") || lower.contains("calculator") -> AppCategory.PRODUCTIVITY

            lower.contains("youtube") || lower.contains("music") || lower.contains("video") ||
            lower.contains("netflix") || lower.contains("media") || lower.contains("photo") ||
            lower.contains("camera") || lower.contains("gallery") -> AppCategory.MEDIA

            lower.contains("setting") || lower.contains("file") || lower.contains("clock") ||
            lower.contains("browser") || lower.contains("chrome") || lower.contains("tool") -> AppCategory.TOOLS

            else -> AppCategory.OTHERS
        }
    }
}
