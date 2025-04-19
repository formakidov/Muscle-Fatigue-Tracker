package com.promni.mft.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expected_recovery",
    foreignKeys = [
        ForeignKey(
            entity = MuscleEntity::class,
            parentColumns = ["id"],
            childColumns = ["muscleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["muscleId"], unique = true)]
)
data class ExpectedRecoveryEntity(
    @PrimaryKey val muscleId: MuscleId,
    val timestamp: Long,
    val lastUpdated: Long
)
