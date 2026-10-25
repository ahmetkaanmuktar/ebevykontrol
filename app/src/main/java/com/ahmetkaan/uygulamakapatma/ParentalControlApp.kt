package com.ahmetkaan.uygulamakapatma

import android.app.Application
import com.ahmetkaan.uygulamakapatma.data.local.AppDatabase

/**
 * Uygulama sınıfı.
 * Room veritabanı instance'ını uygulama düzeyinde başlatır.
 * Tüm bileşenler aynı database instance'ını kullanır.
 */
class ParentalControlApp : Application() {

    // Veritabanı - lazy olarak ilk kullanımda oluşturulur
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        // Uygulama instance'ına global erişim (gerektiğinde kullanılır)
        @Volatile
        private var instance: ParentalControlApp? = null

        fun getInstance(): ParentalControlApp {
            return instance ?: throw IllegalStateException(
                "ParentalControlApp henüz başlatılmadı"
            )
        }
    }
}
