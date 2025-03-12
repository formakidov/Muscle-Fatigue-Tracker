package com.promni.mft.domain.util

import com.promni.mft.data.local.entities.Recovery

object RecoveryCalculator {

    fun calculateExpectedRecovery(fatigue: Float, totalRecoveryTime: Recovery): Long {
        val recoveryTimeMillis = (totalRecoveryTime * (fatigue / 100f)).toLong()
        return System.currentTimeMillis() + recoveryTimeMillis
    }

    fun calculateCurrentFatigue(expectedRecoveryTimestamp: Long?, totalRecoveryTime: Recovery): Float {
        if (expectedRecoveryTimestamp == null) return 0f
        return (100f * (expectedRecoveryTimestamp - System.currentTimeMillis()) / totalRecoveryTime).coerceIn(0f, 100f)
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
