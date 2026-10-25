package com.ahmetkaan.uygulamakapatma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ebeveyn güvenlik ve PIN ayarlarını tutan Entity.
 * Kriptografik tuz (salt), PBKDF2 hash'i, brute-force kilit bilgisi
 * ve PIN unutma durumunda kurtarma sağlayan güvenlik sorusu içerir.
 */
@Entity(tableName = "parent_settings")
data class ParentSettings(
    @PrimaryKey
    val id: Int = 1,

    // PBKDF2WithHmacSHA256 ile hash'lenmiş PIN kodu
    val pinHash: String,

    // Her kuruluma özel 16 baytlık kriptografik tuz (Hex)
    val salt: String,

    // Ardışık hatalı PIN giriş sayısı
    val failedAttempts: Int = 0,

    // Kilitlenme bitiş zamanı (Epoch millis). Bu tarihe kadar giriş yapılamaz
    val lockoutUntilMillis: Long = 0L,

    // PIN kurtarma için seçilen güvenlik sorusu
    val securityQuestion: String = "",

    // Standartlaştırılmış ve hash'lenmiş güvenlik sorusu cevabı
    val securityAnswerHash: String = ""
)
