package com.ahmetkaan.uygulamakapatma

import com.ahmetkaan.uygulamakapatma.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun testPinHashAndVerification_success() {
        val pin = "1234"
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashWithSalt(pin, salt)

        assertTrue(SecurityUtils.verify(pin, hash, salt))
    }

    @Test
    fun testPinVerification_wrongPinFails() {
        val pin = "1234"
        val wrongPin = "4321"
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashWithSalt(pin, salt)

        assertFalse(SecurityUtils.verify(wrongPin, hash, salt))
    }

    @Test
    fun testSaltUniqueness() {
        val salt1 = SecurityUtils.generateSalt()
        val salt2 = SecurityUtils.generateSalt()

        assertNotEquals(salt1, salt2)
    }

    @Test
    fun testLockoutCalculation() {
        assertEquals(0L, SecurityUtils.calculateLockoutMillis(0))
        assertEquals(0L, SecurityUtils.calculateLockoutMillis(2))
        assertEquals(30_000L, SecurityUtils.calculateLockoutMillis(3))
        assertEquals(60_000L, SecurityUtils.calculateLockoutMillis(4))
        assertEquals(300_000L, SecurityUtils.calculateLockoutMillis(5))
        assertEquals(900_000L, SecurityUtils.calculateLockoutMillis(6))
        assertEquals(900_000L, SecurityUtils.calculateLockoutMillis(10))
    }

    @Test
    fun testSecurityAnswerNormalization() {
        val salt = SecurityUtils.generateSalt()
        val answer = "  AnKaRa  "
        val hash = SecurityUtils.hashSecurityAnswer(answer, salt)

        assertTrue(SecurityUtils.verifySecurityAnswer("ankara", hash, salt))
        assertTrue(SecurityUtils.verifySecurityAnswer("ANKARA", hash, salt))
        assertTrue(SecurityUtils.verifySecurityAnswer("  Ankara  ", hash, salt))
        assertFalse(SecurityUtils.verifySecurityAnswer("istanbul", hash, salt))
    }
}
