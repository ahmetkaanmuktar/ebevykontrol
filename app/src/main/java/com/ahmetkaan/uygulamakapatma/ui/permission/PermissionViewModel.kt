package com.ahmetkaan.uygulamakapatma.ui.permission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmetkaan.uygulamakapatma.util.PermissionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 2027 İzin Merkezi ViewModel.
 * İzin geçişlerini canlı izler, az önce verilen izni tespit edip UI'a bildirim verir.
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    init {
        refreshPermissions()
    }

    fun refreshPermissions() {
        val context = getApplication<Application>().applicationContext
        val prev = _uiState.value

        val newUsage = PermissionChecker.hasUsageStatsPermission(context)
        val newOverlay = PermissionChecker.hasOverlayPermission(context)
        val newAccess = PermissionChecker.hasAccessibilityPermission(context)
        val newBattery = PermissionChecker.isBatteryOptimizationIgnored(context)
        val newNotif = PermissionChecker.hasNotificationPermission(context)

        // Yeni verilen bir izin varsa tespit et
        val newlyGranted = when {
            !prev.usageStatsGranted && newUsage -> PermissionType.USAGE
            !prev.accessibilityGranted && newAccess -> PermissionType.ACCESSIBILITY
            !prev.overlayGranted && newOverlay -> PermissionType.OVERLAY
            !prev.batteryOptIgnored && newBattery -> PermissionType.BATTERY
            !prev.notificationGranted && newNotif -> PermissionType.NOTIFICATION
            else -> null
        }

        val isSideloaded = PermissionChecker.isSideloaded(context)
        val needsUnlock = PermissionChecker.mayNeedRestrictedSettingsUnlock(context) && !newAccess

        _uiState.update {
            it.copy(
                usageStatsGranted = newUsage,
                overlayGranted = newOverlay,
                accessibilityGranted = newAccess,
                batteryOptIgnored = newBattery,
                notificationGranted = newNotif,
                isSideloaded = isSideloaded,
                needsRestrictedUnlock = needsUnlock,
                justGrantedPermission = newlyGranted ?: it.justGrantedPermission
            )
        }
    }

    fun clearJustGranted() {
        _uiState.update { it.copy(justGrantedPermission = null) }
    }
}
