package com.promni.mft.domain.model


data class FatigueLog(
    val id: Long,
    val value: Float,
    val muscleId: Long,
    val timestamp: Long
)
