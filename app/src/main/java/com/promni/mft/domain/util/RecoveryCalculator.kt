package com.promni.mft.domain.util

import com.promni.mft.data.local.entities.Recovery

object RecoveryCalculator {

    fun calculateOriginalTimestamp(currentFatigue: Float, expectedRecoveryTimestamp: Long, totalRecoveryTime: Recovery): Long {
        val recoveryTimeMillis = (totalRecoveryTime * (currentFatigue / 100f)).toLong()
        return expectedRecoveryTimestamp - recoveryTimeMillis
    }

    fun calculateExpectedRecoveryTimestamp(originalTimestamp: Long, fatigue: Float, totalRecoveryTime: Recovery): Long {
        val recoveryTimeMillis = (totalRecoveryTime * (fatigue / 100f)).toLong()
        return originalTimestamp + recoveryTimeMillis
    }

    fun calculateCurrentFatigue(expectedRecoveryTimestamp: Long?, totalRecoveryTime: Recovery): Float {
        if (expectedRecoveryTimestamp == null) return 0f
        return (100f * (expectedRecoveryTimestamp - System.currentTimeMillis()) / totalRecoveryTime).coerceIn(0f, 100f)
    }
}
