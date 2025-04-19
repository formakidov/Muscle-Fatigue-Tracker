package com.promni.mft.domain.model

import com.promni.mft.data.local.entities.Recovery

data class MuscleInfo(
    val muscle: Muscle,
    val fatigue: Float,
    val expectedRecovery: Recovery,
    val totalRecoveryTime: Recovery,
)
