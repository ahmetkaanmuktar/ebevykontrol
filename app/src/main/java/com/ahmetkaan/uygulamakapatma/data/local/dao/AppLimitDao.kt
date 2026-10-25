package com.ahmetkaan.uygulamakapatma.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLimitDao {

    @Query("SELECT * FROM app_limits")
    fun getAllLimits(): Flow<List<AppLimit>>

    @Query("SELECT * FROM app_limits")
    suspend fun getAllLimitsSync(): List<AppLimit>

    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitByPackageName(packageName: String): AppLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppLimit(appLimit: AppLimit)

    @Update
    suspend fun updateAppLimit(appLimit: AppLimit)

    @Delete
    suspend fun deleteAppLimit(appLimit: AppLimit)

    @Query("DELETE FROM app_limits WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("UPDATE app_limits SET extensionMinutes = extensionMinutes + :extraMinutes, extensionDateMillis = :dateMillis WHERE packageName = :packageName")
    suspend fun addExtensionMinutes(packageName: String, extraMinutes: Int, dateMillis: Long)

    @Query("UPDATE app_limits SET isPermanentlyBlocked = :isBlocked WHERE packageName = :packageName")
    suspend fun setPermanentlyBlocked(packageName: String, isBlocked: Boolean)
}
