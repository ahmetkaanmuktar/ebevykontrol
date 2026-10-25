package com.ahmetkaan.uygulamakapatma.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * İlk kurulum (Onboarding) tamamlanma durumunu kalıcı olarak saklayan yardımcı sınıf.
 */
class OnboardingPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_COMPLETED, value).apply()

    companion object {
        private const val KEY_COMPLETED = "key_onboarding_completed"
    }
}
