package com.ahmetkaan.uygulamakapatma

import com.ahmetkaan.uygulamakapatma.util.UsageTrackerManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class UsageTrackerManagerTest {

    @Test
    fun testStartOfDayIsMidnight() {
        val zoneId = ZoneId.of("Europe/Istanbul")
        val startOfDayMillis = UsageTrackerManager.getStartOfDayMillis(zoneId)

        val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startOfDayMillis), zoneId)

        assertEquals(0, zonedDateTime.hour)
        assertEquals(0, zonedDateTime.minute)
        assertEquals(0, zonedDateTime.second)
        assertEquals(0, zonedDateTime.nano)
    }

    @Test
    fun testIsToday_detectsCurrentTimeAsToday() {
        val zoneId = ZoneId.of("UTC")
        val now = System.currentTimeMillis()

        assertTrue(UsageTrackerManager.isToday(now, zoneId))
    }

    @Test
    fun testIsToday_detectsYesterdayAsNotToday() {
        val zoneId = ZoneId.of("UTC")
        val yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000L + 1000L)

        assertFalse(UsageTrackerManager.isToday(yesterday, zoneId))
    }
}
