package com.ahmetkaan.uygulamakapatma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahmetkaan.uygulamakapatma.data.local.entity.ParentSettings

/**
 * Ebeveyn ayarları DAO.
 * Güvenli PIN kaydı, brute-force kilit sayaçları ve kurtarma işlemlerini yönetir.
 */
@Dao
interface ParentSettingsDao {

    @Query("SELECT * FROM parent_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ParentSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ParentSettings)

    @Query("SELECT COUNT(*) FROM parent_settings WHERE id = 1 AND pinHash != ''")
    suspend fun isPinSet(): Int

    @Query("UPDATE parent_settings SET failedAttempts = :failedAttempts, lockoutUntilMillis = :lockoutUntil WHERE id = 1")
    suspend fun updateLockout(failedAttempts: Int, lockoutUntil: Long)

    @Query("UPDATE parent_settings SET failedAttempts = 0, lockoutUntilMillis = 0 WHERE id = 1")
    suspend fun resetLockout()
}
