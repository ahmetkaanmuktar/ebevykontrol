package com.ahmetkaan.uygulamakapatma.data.repository

import com.ahmetkaan.uygulamakapatma.data.local.dao.ParentSettingsDao
import com.ahmetkaan.uygulamakapatma.data.local.entity.ParentSettings
import com.ahmetkaan.uygulamakapatma.util.SecurityUtils

sealed class PinVerificationResult {
    data object Success : PinVerificationResult()
    data class LockedOut(val remainingSeconds: Long) : PinVerificationResult()
    data class InvalidPin(val failedAttempts: Int, val lockoutSeconds: Long) : PinVerificationResult()
    data object NotConfigured : PinVerificationResult()
}

/**
 * Ebeveyn PIN ve güvenlik mantığını yöneten Repository.
 * PBKDF2 tuzlama, sabit-zamanlı karşılaştırma ve kilitlenme (brute-force) koruması sağlar.
 */
class ParentSettingsRepository(private val dao: ParentSettingsDao) {

    suspend fun getSettings(): ParentSettings? = dao.getSettings()

    suspend fun isPinSet(): Boolean = dao.isPinSet() > 0

    /**
     * İlk kez PIN ve güvenlik sorusu oluşturur.
     */
    suspend fun setupPin(
        rawPin: String,
        securityQuestion: String = "",
        securityAnswer: String = ""
    ) {
        val salt = SecurityUtils.generateSalt()
        val pinHash = SecurityUtils.hashWithSalt(rawPin, salt)
        val answerHash = if (securityAnswer.isNotBlank()) {
            SecurityUtils.hashSecurityAnswer(securityAnswer, salt)
        } else ""

        val settings = ParentSettings(
            id = 1,
            pinHash = pinHash,
            salt = salt,
            failedAttempts = 0,
            lockoutUntilMillis = 0L,
            securityQuestion = securityQuestion,
            securityAnswerHash = answerHash
        )
        dao.saveSettings(settings)
    }

    /**
     * PIN'i doğrular. Kilit süresini ve hatalı denemeleri kontrol eder.
     */
    suspend fun verifyPin(rawPin: String): PinVerificationResult {
        val settings = dao.getSettings() ?: return PinVerificationResult.NotConfigured
        val now = System.currentTimeMillis()

        // 1. Kilit kontrolü
        if (settings.lockoutUntilMillis > now) {
            val remainingSec = (settings.lockoutUntilMillis - now) / 1000
            return PinVerificationResult.LockedOut(remainingSec.coerceAtLeast(1))
        }

        // 2. Doğrulama
        val isValid = SecurityUtils.verify(rawPin, settings.pinHash, settings.salt)
        return if (isValid) {
            // Başarılı giriş: Hatalı deneme sayacını sıfırla
            if (settings.failedAttempts > 0 || settings.lockoutUntilMillis > 0L) {
                dao.resetLockout()
            }
            PinVerificationResult.Success
        } else {
            // Hatalı giriş: Sayacı artır ve gerekiyorsa kilit koy
            val newFailed = settings.failedAttempts + 1
            val lockoutDuration = SecurityUtils.calculateLockoutMillis(newFailed)
            val newLockoutUntil = if (lockoutDuration > 0) now + lockoutDuration else 0L

            dao.updateLockout(newFailed, newLockoutUntil)

            val lockoutSec = lockoutDuration / 1000
            if (lockoutSec > 0) {
                PinVerificationResult.LockedOut(lockoutSec)
            } else {
                PinVerificationResult.InvalidPin(newFailed, 0L)
            }
        }
    }

    /**
     * PIN unutulduğunda güvenlik sorusu cevabıyla PIN'i sıfırlar.
     */
    suspend fun resetPinWithSecurityAnswer(
        answer: String,
        newRawPin: String
    ): Boolean {
        val settings = dao.getSettings() ?: return false
        if (settings.securityAnswerHash.isBlank()) return false

        val isAnswerCorrect = SecurityUtils.verify(
            answer.trim().lowercase(),
            settings.securityAnswerHash,
            settings.salt
        )

        return if (isAnswerCorrect) {
            val newSalt = SecurityUtils.generateSalt()
            val newPinHash = SecurityUtils.hashWithSalt(newRawPin, newSalt)
            val newAnswerHash = SecurityUtils.hashSecurityAnswer(answer, newSalt)

            dao.saveSettings(
                settings.copy(
                    pinHash = newPinHash,
                    salt = newSalt,
                    failedAttempts = 0,
                    lockoutUntilMillis = 0L,
                    securityAnswerHash = newAnswerHash
                )
            )
            true
        } else {
            false
        }
    }

    /**
     * Güvenlik sorusunu döner.
     */
    suspend fun getSecurityQuestion(): String? {
        return dao.getSettings()?.securityQuestion?.takeIf { it.isNotBlank() }
    }
}
