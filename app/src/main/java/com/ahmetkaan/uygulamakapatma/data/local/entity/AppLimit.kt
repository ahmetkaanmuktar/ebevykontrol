package com.ahmetkaan.uygulamakapatma.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Kısıtlanan uygulamaların kurallarını tutan Entity.
 * Günlük kullanım süresi `UsageTrackerManager` tarafından dinamik olarak
 * anlık hesaplandığı için veritabanında geçersiz sayaçlar tutulmaz.
 */
@Entity(
    tableName = "app_limits",
    indices = [
        Index(value = ["packageName"], unique = true)
    ]
)
data class AppLimit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val packageName: String,

    // Ebeveynin belirlediği günlük maksimum süre (dakika)
    val dailyLimitMinutes: Int,

    // Uygulama kalıcı olarak engellenmiş mi?
    val isPermanentlyBlocked: Boolean = false,

    // Ebeveyn PIN ile eklenen geçici ek süre (dakika)
    val extensionMinutes: Int = 0,

    // Ek sürenin verildiği zaman (Epoch millis) - gün aşımında sıfır kabul edilir
    val extensionDateMillis: Long = 0L
) {
    fun getEffectiveDailyLimitMinutes(isExtensionToday: Boolean): Int {
        val extra = if (isExtensionToday) extensionMinutes else 0
        return dailyLimitMinutes + extra
    }
}
