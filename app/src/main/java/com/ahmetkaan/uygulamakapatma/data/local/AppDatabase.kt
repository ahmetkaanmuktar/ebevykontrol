package com.ahmetkaan.uygulamakapatma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ahmetkaan.uygulamakapatma.data.local.dao.AppLimitDao
import com.ahmetkaan.uygulamakapatma.data.local.dao.ParentSettingsDao
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import com.ahmetkaan.uygulamakapatma.data.local.entity.ParentSettings

/**
 * Room veritabanının ana sınıfı.
 * Tüm Entity'leri ve DAO'ları bir araya getirir.
 */
@Database(
    entities = [
        AppLimit::class,
        ParentSettings::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appLimitDao(): AppLimitDao
    abstract fun parentSettingsDao(): ParentSettingsDao

    companion object {
        // Room veritabanının tek bir instance olarak kullanılmasını sağlar (Singleton)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "parental_control_db"
            )
                // İleride migration stratejisi eklenebilir
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
