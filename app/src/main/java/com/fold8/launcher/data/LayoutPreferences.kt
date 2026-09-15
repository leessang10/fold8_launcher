package com.fold8.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fold8_launcher_prefs")

/**
 * 커버/메인 화면 레이아웃 설정 및 독(Dock) 고정 앱 관리
 */
class LayoutPreferences(private val context: Context) {

    companion object {
        private val KEY_MIRROR_LAYOUT = booleanPreferencesKey("key_mirror_cover_layout")
        private val KEY_TASKBAR_AUTO_HIDE = booleanPreferencesKey("key_taskbar_auto_hide")
        private val KEY_COVER_DOCK = stringSetPreferencesKey("key_cover_dock_packages")
        private val KEY_MAIN_DOCK = stringSetPreferencesKey("key_main_dock_packages")
    }

    val isMirrorLayout: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MIRROR_LAYOUT] ?: false
    }

    val isTaskbarAutoHide: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_TASKBAR_AUTO_HIDE] ?: false
    }

    val coverDockPackages: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_COVER_DOCK] ?: setOf(
            "com.samsung.android.dialer",
            "com.samsung.android.messaging",
            "com.sec.android.app.sbrowser",
            "com.sec.android.app.camera"
        )
    }

    val mainDockPackages: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_MAIN_DOCK] ?: setOf(
            "com.samsung.android.dialer",
            "com.sec.android.app.sbrowser",
            "com.samsung.android.app.notes",
            "com.sec.android.gallery3d",
            "com.google.android.youtube",
            "com.sec.android.app.camera"
        )
    }

    suspend fun setMirrorLayout(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MIRROR_LAYOUT] = enabled }
    }

    suspend fun setTaskbarAutoHide(enabled: Boolean) {
        context.dataStore.edit { it[KEY_TASKBAR_AUTO_HIDE] = enabled }
    }
}
