package com.fold8.launcher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fold8.launcher.data.AppRepository
import com.fold8.launcher.data.LayoutPreferences
import com.fold8.launcher.domain.model.AppCategory
import com.fold8.launcher.domain.model.AppItem
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
    val layoutPreferences = LayoutPreferences(application)

    val installedApps: StateFlow<List<AppItem>> = appRepository.installedApps

    private val _recentApps = MutableStateFlow<List<AppItem>>(emptyList())
    val recentApps: StateFlow<List<AppItem>> = _recentApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    private val _isAppDrawerOpen = MutableStateFlow(false)
    val isAppDrawerOpen: StateFlow<Boolean> = _isAppDrawerOpen.asStateFlow()

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

    override fun onCleared() {
        super.onCleared()
        appRepository.unregisterReceiver()
    }
}
