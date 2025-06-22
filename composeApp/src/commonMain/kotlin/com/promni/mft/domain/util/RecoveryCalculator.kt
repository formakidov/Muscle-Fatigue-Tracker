package com.promni.mft.domain.util

import kotlin.math.exp

object RecoveryCalculator {

    fun calculateExpectedRecovery(fatigue: Float, totalRecoveryTime: Recovery): Long {
        val recoveryTimeMillis = (totalRecoveryTime * (fatigue / 100f)).toLong()
        return SystemTime.nowMillis() + recoveryTimeMillis
    }

    fun calculateCurrentFatigue(expectedRecoveryTimestamp: Long?, totalRecoveryTime: Recovery): Float {
        if (expectedRecoveryTimestamp == null) return 0f
        return (100f * (expectedRecoveryTimestamp - SystemTime.nowMillis()) / totalRecoveryTime).coerceIn(0f, 100f)
    }

    /*

    f(t) = L / (1 + e^(-k(t - t0)))

    Where:
    *   `f(t)`:  The fatigue level (0 to 100) at time `t`.  *Lower value = more recovered*.  This is the opposite of your current function, where 100 is fully recovered. We will adjust this.
    *   `fatigue`: The maximum value of the curve (the fatigue level at the start of recovery).
    *   `k`: The growth rate (steepness of the curve). Higher `k` means faster recovery.
    *   `t0`: The time of the midpoint of the recovery curve (the inflection point).  This represents the time at which recovery is 50% complete.
    *   `t`: The current time.
    *   `e`: The base of the natural logarithm (approximately 2.71828).
    */

    fun calculateCurrentFatigueSigmoid(
        fatigue: Float,
        expectedRecoveryTimestamp: Long,
        totalRecoveryTime: Long,
        steepness: Double = 1.0, // Higher value = faster recovery.
    ): Float {
        val currentTime = SystemTime.nowMillis()
        if (currentTime >= expectedRecoveryTimestamp) return 0f // Fully recovered

        val t0 = expectedRecoveryTimestamp - totalRecoveryTime / 2.0 // Midpoint of recovery
        val t = currentTime.toDouble()
        val k = steepness * (12.0 / totalRecoveryTime) // Scale k to be relative to total recovery Time

        // Apply the logistic function
        val fatigue = fatigue / (1 + exp(-k * (t - t0)))

        return fatigue.toFloat().coerceIn(0f, 100f)
    }

    /**
     * Example:
     *
     * Initial data: total recovery time: 4 days, fatigue set by user: 100%
     *
     * 1 day past -> expected recovery in 3 day, fatigue is now 75%
     * -------
     * case 1 (user increased total recovery time)
     *  user changes total recovery time to 5 days
     *  expected recovery in 4 days, fatigue becomes 80%
     * -------
     * case 2 (user decreased total recovery time)
     *  user changes total recovery time to 3 days
     *  expected recovery in 2 days, fatigue becomes 66.6%
     *
     *  case 2.1 (user decreased total recovery time so that muscle have just recovered)
     *  user changes total recovery time to 1 day
     *  expected recovery is now, fatigue becomes 0%
     *
     *  case 2.1 (user decreased total recovery time so that muscle has already recovered)
     *  user changes total recovery time to <1 day, e.g. 0.5 days
     *  expected recovery is in the past, e.g. in -0.5 days (12 hours ago fully recovered), fatigue becomes 0%
     *
     */

    fun calculateNewExpectedRecovery(
        currentTotalRecoveryTimePeriod: Long,
        currentExpectedRecoveryTimestamp: Long,
        newTotalRecoveryTimePeriod: Long
    ) = currentExpectedRecoveryTimestamp - currentTotalRecoveryTimePeriod + newTotalRecoveryTimePeriod

}
