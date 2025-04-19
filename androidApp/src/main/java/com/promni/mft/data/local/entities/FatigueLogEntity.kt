package com.promni.mft.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.promni.mft.domain.model.FatigueLog

@Entity(
    tableName = "fatigue_logs",
    foreignKeys = [
        ForeignKey(
            entity = MuscleEntity::class,
            parentColumns = ["id"],
            childColumns = ["muscleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["muscleId"])]
)
data class FatigueLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val muscleId: MuscleId,
    val value: Float,
    val timestamp: Long
)

fun FatigueLogEntity.asExternalModel() = FatigueLog(id, value, muscleId, timestamp)
