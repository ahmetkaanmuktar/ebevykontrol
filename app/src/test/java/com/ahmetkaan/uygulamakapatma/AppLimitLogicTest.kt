package com.ahmetkaan.uygulamakapatma

import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import org.junit.Assert.assertEquals
import org.junit.Test

class AppLimitLogicTest {

    @Test
    fun testEffectiveLimit_withTodayExtension() {
        val limit = AppLimit(
            packageName = "com.google.android.youtube",
            dailyLimitMinutes = 30,
            extensionMinutes = 15,
            extensionDateMillis = System.currentTimeMillis()
        )

        // Bugün ek süre geçerliyse 30 + 15 = 45 dk olmalı
        assertEquals(45, limit.getEffectiveDailyLimitMinutes(isExtensionToday = true))
    }

    @Test
    fun testEffectiveLimit_withExpiredExtension() {
        val limit = AppLimit(
            packageName = "com.google.android.youtube",
            dailyLimitMinutes = 30,
            extensionMinutes = 15,
            extensionDateMillis = System.currentTimeMillis() - 86400000L // Dün
        )

        // Gün aşıldıysa ek süre yansımamalı
        assertEquals(30, limit.getEffectiveDailyLimitMinutes(isExtensionToday = false))
    }
}
