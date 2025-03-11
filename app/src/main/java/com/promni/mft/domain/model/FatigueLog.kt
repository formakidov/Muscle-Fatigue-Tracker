package com.promni.mft.domain.model

import com.promni.mft.data.local.entities.MuscleId

data class FatigueLog(
    val id: Long,
    val muscleId: MuscleId,
    val changeAmount: Float,
    val timestamp: Long
)
