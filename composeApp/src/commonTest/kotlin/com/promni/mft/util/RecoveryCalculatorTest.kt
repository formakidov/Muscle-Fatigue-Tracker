package com.promni.mft.util

import com.promni.mft.domain.util.RecoveryCalculator.calculateNewExpectedRecovery
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class RecoveryCalculatorTest {

    private val oneDayMillis = 24 * 60 * 60 * 1000L

    @Test
    fun `case 1 - user increases total recovery time`() {
        val now = Clock.System.now().toEpochMilliseconds()

        val currentTotalRecoveryTimePeriod = 4 * oneDayMillis // 4 days
        val daysPast = 1
        val currentExpectedRecoveryTimestamp = now + (currentTotalRecoveryTimePeriod - (daysPast * oneDayMillis))  // 3 days from now
        val newTotalRecoveryTimePeriod = 5 * oneDayMillis // 5 days

        val expected = now + (4 * oneDayMillis) // Expected recovery shifts to 4 days from now

        val result = calculateNewExpectedRecovery(
            currentTotalRecoveryTimePeriod,
            currentExpectedRecoveryTimestamp,
            newTotalRecoveryTimePeriod
        )

        assertEquals(expected, result, "Expected recovery should be in 4 days")
    }

    @Test
    fun `case 2 - user decreases total recovery time`() {
        val now = Clock.System.now().toEpochMilliseconds()

        val currentTotalRecoveryTimePeriod = 4 * oneDayMillis // 4 days
        val daysPast = 1
        val currentExpectedRecoveryTimestamp = now + (currentTotalRecoveryTimePeriod - (daysPast * oneDayMillis))  // 3 days from now
        val newTotalRecoveryTimePeriod = 3 * oneDayMillis // 3 days

        val expected = now + (2 * oneDayMillis) // Expected recovery shifts to 2 days from now

        val result = calculateNewExpectedRecovery(
            currentTotalRecoveryTimePeriod,
            currentExpectedRecoveryTimestamp,
            newTotalRecoveryTimePeriod
        )

        assertEquals(expected, result, "Expected recovery should be in 2 days")
    }

    @Test
    fun `case 2_1 - user decreases total recovery time, recovery has just completed`() {
        val now = Clock.System.now().toEpochMilliseconds()

        val currentTotalRecoveryTimePeriod = 4 * oneDayMillis // 4 days
        val daysPast = 1
        val currentExpectedRecoveryTimestamp = now + (currentTotalRecoveryTimePeriod - (daysPast * oneDayMillis)) // 3 days from now
        val newTotalRecoveryTimePeriod = (1 * oneDayMillis).toLong() // 1 day

        val expected = now // Recovery should be marked as completed

        val result = calculateNewExpectedRecovery(
            currentTotalRecoveryTimePeriod,
            currentExpectedRecoveryTimestamp,
            newTotalRecoveryTimePeriod
        )

        assertEquals(expected, result, "Expected recovery should be now (fully recovered)")
    }

    @Test
    fun `case 2_2 - user decreases total recovery time, recovery has completed in the past`() {
        val now = Clock.System.now().toEpochMilliseconds()

        val currentTotalRecoveryTimePeriod = 4 * oneDayMillis // 4 days
        val daysPast = 2
        val currentExpectedRecoveryTimestamp = now + (currentTotalRecoveryTimePeriod - (daysPast * oneDayMillis))  // 2 days from now
        val newTotalRecoveryTimePeriod = (1 * oneDayMillis).toLong() // 1 day

        val expected = now - (1 * oneDayMillis) // Recovered 1 day ago

        val result = calculateNewExpectedRecovery(
            currentTotalRecoveryTimePeriod,
            currentExpectedRecoveryTimestamp,
            newTotalRecoveryTimePeriod
        )

        assertEquals(expected, result, "Expected recovery should be 1 day ago")
    }
}
