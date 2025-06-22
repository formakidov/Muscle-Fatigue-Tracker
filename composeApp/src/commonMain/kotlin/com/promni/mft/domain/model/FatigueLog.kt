package com.promni.mft.domain.model

import com.promni.mft.domain.util.MuscleId

data class FatigueLog(
    val id: Long,
    val value: Float,
    val muscleId: MuscleId,
    val timestamp: Long
)
