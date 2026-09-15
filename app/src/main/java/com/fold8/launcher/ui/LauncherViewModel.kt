package com.fold8.launcher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fold8.launcher.data.AppPairRepository
import com.fold8.launcher.data.AppRepository
import com.fold8.launcher.data.LayoutPreferences
import com.fold8.launcher.domain.model.AppCategory
import com.fold8.launcher.domain.model.AppItem
import com.fold8.launcher.domain.model.AppPairItem
import com.fold8.launcher.domain.model.AppTrioItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    val appRepository = AppRepository(application)
    val appPairRepository = AppPairRepository(application, appRepository)
    val layoutPreferences = LayoutPreferences(application)

    val installedApps: StateFlow<List<AppItem>> = appRepository.installedApps
    val appPairs: StateFlow<List<AppPairItem>> = appPairRepository.appPairs
    val appTrios: StateFlow<List<AppTrioItem>> = appPairRepository.appTrios

    private val _recentApps = MutableStateFlow<List<AppItem>>(emptyList())
    val recentApps: StateFlow<List<AppItem>> = _recentApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    private val _isAppDrawerOpen = MutableStateFlow(false)
    val isAppDrawerOpen: StateFlow<Boolean> = _isAppDrawerOpen.asStateFlow()

    private val _isCreatePairDialogOpen = MutableStateFlow(false)
    val isCreatePairDialogOpen: StateFlow<Boolean> = _isCreatePairDialogOpen.asStateFlow()

    private val _isCreateTrioDialogOpen = MutableStateFlow(false)
    val isCreateTrioDialogOpen: StateFlow<Boolean> = _isCreateTrioDialogOpen.asStateFlow()

    // 시간대별 지능형 컨텍스트 추천 타이틀 및 앱 목록
    val contextualTitle: String get() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..9 -> "출근/오전 활력 추천"
            in 10..17 -> "업무 & 생산성 집중 모드"
            in 18..22 -> "퇴근 & 미디어 휴식 모드"
            else -> "심야 맞춤 도구"
        }
    }

    val contextualApps: StateFlow<List<AppItem>> = installedApps.map { apps ->
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..9 -> apps.filter { it.category == AppCategory.COMMUNICATION || it.category == AppCategory.TOOLS }.take(6)
            in 10..17 -> apps.filter { it.category == AppCategory.PRODUCTIVITY || it.category == AppCategory.TOOLS }.take(6)
            in 18..22 -> apps.filter { it.category == AppCategory.MEDIA || it.category == AppCategory.COMMUNICATION }.take(6)
            else -> apps.filter { it.category == AppCategory.TOOLS }.take(6)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApps: StateFlow<List<AppItem>> = combine(
        installedApps,
        _searchQuery,
        _selectedCategory
    ) { apps, query, category ->
        apps.filter { app ->
            val matchQuery = query.isBlank() || app.label.contains(query, ignoreCase = true)
            val matchCategory = category == AppCategory.ALL || app.category == category
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun launchApp(app: AppItem) {
        recordRecent(app)
        appRepository.launchApp(app)
    }

    fun launchAppAdjacent(app: AppItem) {
        recordRecent(app)
        appRepository.launchAppAdjacent(app)
    }

    fun launchAppPair(pair: AppPairItem) {
        recordRecent(pair.primaryApp)
        recordRecent(pair.secondaryApp)
        appPairRepository.launchAppPair(pair)
    }

    fun launchAppTrio(trio: AppTrioItem) {
        recordRecent(trio.primaryApp)
        recordRecent(trio.secondaryApp)
        recordRecent(trio.tertiaryApp)
        appPairRepository.launchAppTrio(trio)
    }

    fun setCreateTrioDialogOpen(open: Boolean) {
        _isCreateTrioDialogOpen.value = open
    }

    fun createAndSaveTrio(title: String, first: AppItem, second: AppItem, third: AppItem) {
        appPairRepository.addAppTrio(title, first, second, third)
        _isCreateTrioDialogOpen.value = false
    }

    fun removeTrio(trioId: String) {
        appPairRepository.removeAppTrio(trioId)
    }

    private fun recordRecent(app: AppItem) {
        val current = _recentApps.value.toMutableList()
        current.removeAll { it.id == app.id }
        current.add(0, app)
        if (current.size > 8) {
            _recentApps.value = current.take(8)
        } else {
            _recentApps.value = current
        }
    }

    fun setAppDrawerOpen(open: Boolean) {
        _isAppDrawerOpen.value = open
        if (!open) {
            _searchQuery.value = ""
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun setCreatePairDialogOpen(open: Boolean) {
        _isCreatePairDialogOpen.value = open
    }

    fun createAndSavePair(title: String, first: AppItem, second: AppItem) {
        appPairRepository.addAppPair(title, first, second)
        _isCreatePairDialogOpen.value = false
    }

    fun removePair(pairId: String) {
        appPairRepository.removeAppPair(pairId)
    }

    override fun onCleared() {
        super.onCleared()
        appRepository.unregisterReceiver()
    }
}
