package com.ahmetkaan.uygulamakapatma.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahmetkaan.uygulamakapatma.ParentalControlApp
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import com.ahmetkaan.uygulamakapatma.data.repository.AppLimitRepository
import com.ahmetkaan.uygulamakapatma.util.AppInfo
import com.ahmetkaan.uygulamakapatma.util.InstalledAppsManager
import com.ahmetkaan.uygulamakapatma.util.UsageTrackerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppFilter(val label: String) {
    ALL("Tümü"),
    RESTRICTED("Kısıtlananlar"),
    EXCEEDED("Süresi Dolanlar"),
    BLOCKED("Kilitliler"),
    TOP_USAGE("En Çok Kullanılanlar")
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val apps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: AppFilter = AppFilter.ALL,
    val totalScreenTimeMinutes: Int = 0,
    val restrictedAppsCount: Int = 0,
    val exceededAppsCount: Int = 0,
    val blockedAppsCount: Int = 0,
    val topAppName: String = "",
    val topAppMinutes: Int = 0,
    val isInstantLockdownActive: Boolean = false
) {
    val filteredApps: List<AppInfo>
        get() {
            var result = apps

            // Kategori filtrelemesi
            result = when (activeFilter) {
                AppFilter.ALL -> result
                AppFilter.RESTRICTED -> result.filter { it.limit != null }
                AppFilter.EXCEEDED -> result.filter { app ->
                    val limit = app.limit?.dailyLimitMinutes ?: 0
                    limit > 0 && app.todayUsageMinutes >= limit
                }
                AppFilter.BLOCKED -> result.filter { it.limit?.isPermanentlyBlocked == true }
                AppFilter.TOP_USAGE -> result.filter { it.todayUsageMinutes > 0 }
                    .sortedByDescending { it.todayUsageMinutes }
            }

            // Metin araması
            if (searchQuery.isNotBlank()) {
                result = result.filter {
                    it.appName.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                }
            }

            return result
        }
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ParentalControlApp
    private val limitRepo = AppLimitRepository(app.database.appLimitDao())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val context = getApplication<Application>().applicationContext
            val savedLimits = limitRepo.getAllLimitsSync().associateBy { it.packageName }
            val usages = UsageTrackerManager.getAllAppsUsageToday(context)

            val installedApps = InstalledAppsManager.getInstalledLauncherApps(
                context = context,
                savedLimits = savedLimits,
                todayUsages = usages
            )

            val totalScreenTime = usages.values.sum()
            val restrictedCount = savedLimits.values.count { it.dailyLimitMinutes > 0 || it.isPermanentlyBlocked }
            val blockedCount = savedLimits.values.count { it.isPermanentlyBlocked }
            val exceededCount = installedApps.count { app ->
                val lim = app.limit?.dailyLimitMinutes ?: 0
                lim > 0 && app.todayUsageMinutes >= lim
            }

            val topApp = installedApps.maxByOrNull { it.todayUsageMinutes }

            val allRestrictedBlocked = restrictedCount > 0 &&
                    savedLimits.values.filter { it.dailyLimitMinutes > 0 }.all { it.isPermanentlyBlocked }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    apps = installedApps,
                    totalScreenTimeMinutes = totalScreenTime,
                    restrictedAppsCount = restrictedCount,
                    blockedAppsCount = blockedCount,
                    exceededAppsCount = exceededCount,
                    topAppName = if ((topApp?.todayUsageMinutes ?: 0) > 0) topApp?.appName ?: "" else "",
                    topAppMinutes = topApp?.todayUsageMinutes ?: 0,
                    isInstantLockdownActive = allRestrictedBlocked
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: AppFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
    }

    fun toggleInstantLockdown(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val allLimits = limitRepo.getAllLimitsSync()
            allLimits.forEach { limit ->
                limitRepo.updateAppLimit(limit.copy(isPermanentlyBlocked = enable))
            }
            loadData()
        }
    }

    fun setAppLimit(packageName: String, dailyLimitMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = limitRepo.getLimitByPackageName(packageName)
            val updated = existing?.copy(dailyLimitMinutes = dailyLimitMinutes)
                ?: AppLimit(
                    packageName = packageName,
                    dailyLimitMinutes = dailyLimitMinutes
                )
            limitRepo.saveAppLimit(updated)
            loadData()
        }
    }

    fun togglePermanentBlock(packageName: String, isBlocked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = limitRepo.getLimitByPackageName(packageName)
            if (existing != null) {
                limitRepo.updateAppLimit(existing.copy(isPermanentlyBlocked = isBlocked))
            } else {
                limitRepo.saveAppLimit(
                    AppLimit(
                        packageName = packageName,
                        dailyLimitMinutes = 0,
                        isPermanentlyBlocked = isBlocked
                    )
                )
            }
            loadData()
        }
    }

    fun removeLimit(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            limitRepo.deleteByPackageName(packageName)
            loadData()
        }
    }
}
