package com.ahmetkaan.uygulamakapatma.ui.permission

enum class PermissionType {
    USAGE,
    OVERLAY,
    ACCESSIBILITY,
    BATTERY,
    NOTIFICATION
}

/**
 * 2027 İzin Merkezi UI Durumu
 */
data class PermissionUiState(
    val usageStatsGranted: Boolean = false,
    val overlayGranted: Boolean = false,
    val accessibilityGranted: Boolean = false,
    val batteryOptIgnored: Boolean = false,
    val notificationGranted: Boolean = false,
    val isSideloaded: Boolean = false,
    val needsRestrictedUnlock: Boolean = false,
    val justGrantedPermission: PermissionType? = null
) {
    // Zorunlu 3 izin içinden verilen sayısı
    val grantedCount: Int
        get() = listOf(
            usageStatsGranted,
            accessibilityGranted,
            overlayGranted
        ).count { it }

    // Zorunlu izin sayısı
    val totalCount: Int
        get() = 3

    // İlerleme yüzdesi (0f - 1f arası)
    val progress: Float
        get() = grantedCount.toFloat() / totalCount.toFloat()

    // Uygulamanın çalışması için zorunlu olan tüm izinler verildi mi?
    val allCoreGranted: Boolean
        get() = usageStatsGranted && overlayGranted && accessibilityGranted
}
