package com.ahmetkaan.uygulamakapatma.data.repository

import com.ahmetkaan.uygulamakapatma.data.local.dao.AppLimitDao
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import com.ahmetkaan.uygulamakapatma.util.UsageTrackerManager
import kotlinx.coroutines.flow.Flow

/**
 * Uygulama limitleri Repository katmanı.
 */
class AppLimitRepository(private val dao: AppLimitDao) {

    fun getAllLimits(): Flow<List<AppLimit>> = dao.getAllLimits()

    suspend fun getAllLimitsSync(): List<AppLimit> = dao.getAllLimitsSync()

    suspend fun getLimitByPackageName(packageName: String): AppLimit? {
        return dao.getLimitByPackageName(packageName)
    }

    suspend fun saveAppLimit(appLimit: AppLimit) {
        dao.insertAppLimit(appLimit)
    }

    suspend fun updateAppLimit(appLimit: AppLimit) {
        dao.updateAppLimit(appLimit)
    }

    suspend fun deleteAppLimit(appLimit: AppLimit) {
        dao.deleteAppLimit(appLimit)
    }

    suspend fun deleteByPackageName(packageName: String) {
        dao.deleteByPackageName(packageName)
    }

    /**
     * Ebeveyn PIN doğruladığında bugün geçerli olmak üzere ek süre tanır (örn: 15 dk).
     */
    suspend fun addExtensionMinutes(packageName: String, extraMinutes: Int = 15) {
        val now = System.currentTimeMillis()
        val existing = dao.getLimitByPackageName(packageName)
        if (existing != null) {
            val isExtensionFromToday = UsageTrackerManager.isToday(existing.extensionDateMillis)
            val currentExtension = if (isExtensionFromToday) existing.extensionMinutes else 0
            val updated = existing.copy(
                extensionMinutes = currentExtension + extraMinutes,
                extensionDateMillis = now
            )
            dao.updateAppLimit(updated)
        }
    }

    suspend fun setPermanentlyBlocked(packageName: String, isBlocked: Boolean) {
        dao.setPermanentlyBlocked(packageName, isBlocked)
    }
}
