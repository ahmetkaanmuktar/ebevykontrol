package com.ahmetkaan.uygulamakapatma.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Güçlendirilmiş PIN ve güvenlik yardımcı sınıfı.
 * PBKDF2WithHmacSHA256 (10.000 iterasyon) ve 16-byte kriptografik tuz (salt) kullanır.
 * Zamanlama saldırılarına (timing attack) karşı MessageDigest.isEqual ile karşılaştırma yapar.
 * Brute-force saldırılarına karşı kademeli bekleme süresi (lockout) hesaplar.
 */
object SecurityUtils {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    /**
     * Kriptografik olarak güvenli 16 baytlık rastgele tuz üretir ve Hex dizesi olarak döner.
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_BYTES)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verilen PIN ve tuzu PBKDF2WithHmacSHA256 ile hash'ler.
     */
    fun hashWithSalt(input: String, saltHex: String): String {
        val saltBytes = hexToByteArray(saltHex)
        val spec = PBEKeySpec(input.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Girilen PIN ile kayıtlı hash'i sabit zamanlı (constant-time) karşılaştırır.
     */
    fun verify(input: String, storedHashHex: String, saltHex: String): Boolean {
        val computedHash = hashWithSalt(input, saltHex)
        val computedBytes = hexToByteArray(computedHash)
        val storedBytes = hexToByteArray(storedHashHex)
        return MessageDigest.isEqual(computedBytes, storedBytes)
    }

    /**
     * Güvenlik sorusu cevabını standartlaştırıp (küçük harf, kırpılmış) hash'ler.
     */
    fun hashSecurityAnswer(answer: String, saltHex: String): String {
        val normalized = answer.trim().lowercase()
        return hashWithSalt(normalized, saltHex)
    }

    /**
     * Güvenlik sorusu cevabını normalize ederek doğrular.
     */
    fun verifySecurityAnswer(inputAnswer: String, storedHashHex: String, saltHex: String): Boolean {
        val normalized = inputAnswer.trim().lowercase()
        return verify(normalized, storedHashHex, saltHex)
    }

    /**
     * Hatalı deneme sayısına göre bekleme/kilitlenme süresini (milisaniye cinsinden) hesaplar.
     * 1-2 deneme: Kilit yok
     * 3 deneme: 30 saniye
     * 4 deneme: 60 saniye
     * 5 deneme: 300 saniye (5 dakika)
     * 6+ deneme: 900 saniye (15 dakika)
     */
    fun calculateLockoutMillis(failedAttempts: Int): Long {
        return when {
            failedAttempts < 3 -> 0L
            failedAttempts == 3 -> 30_000L
            failedAttempts == 4 -> 60_000L
            failedAttempts == 5 -> 300_000L
            else -> 900_000L
        }
    }

    private fun hexToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = (
                    (Character.digit(hex[i], 16) shl 4) +
                            Character.digit(hex[i + 1], 16)
                    ).toByte()
            i += 2
        }
        return data
    }
}
