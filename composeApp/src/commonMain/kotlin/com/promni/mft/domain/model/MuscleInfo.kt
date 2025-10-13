package com.promni.mft.domain.model



data class MuscleInfo(
    val muscle: Muscle,
    val fatigue: Float,
    val expectedRecovery: Long,
    val totalRecoveryTime: Long,
)
