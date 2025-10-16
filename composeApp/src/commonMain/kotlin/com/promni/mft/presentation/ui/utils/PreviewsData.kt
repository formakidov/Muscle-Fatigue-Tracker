package com.promni.mft.presentation.ui.utils

import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.util.SystemTime

private const val dayMillis: Long = 24 * 60 * 60 * 1000
private const val fourDays: Long = 4 * dayMillis

val muscleAbsNotTrained = MuscleInfo(
    muscle = Muscle(id = 1, name = "Abs", order = 2),
    fatigue = 0f,
    expectedRecovery = 0,
    totalRecoveryTime = fourDays
)
val muscleBicepsEasyTrained = MuscleInfo(
    muscle = Muscle(id = 2, name = "Biceps", order = 0),
    fatigue = 25f,
    expectedRecovery = SystemTime.nowMillis() + dayMillis,
    totalRecoveryTime = fourDays
)
val muscleTricepsMiddleTrained = MuscleInfo(
    muscle = Muscle(id = 3, name = "Triceps", order = 1),
    fatigue = 50f,
    expectedRecovery = SystemTime.nowMillis() + dayMillis * 2,
    totalRecoveryTime = fourDays
)
val muscleQuadricepsHardTrained = MuscleInfo(
    muscle = Muscle(id = 4, name = "Quadriceps", order = 3),
    fatigue = 75f,
    expectedRecovery = SystemTime.nowMillis() + fourDays,
    totalRecoveryTime = fourDays
)

val allMuscles = listOf(muscleAbsNotTrained, muscleBicepsEasyTrained, muscleTricepsMiddleTrained, muscleQuadricepsHardTrained)

val fatigueLogs: List<FatigueLog>
    get() {
        val now = SystemTime.nowMillis()
        val weekMillis = 7 * dayMillis

        return listOf(
            // This week
            FatigueLog(id = 1, value = 25f, muscleId = muscleBicepsEasyTrained.muscle.id, timestamp = now - dayMillis),
            FatigueLog(id = 2, value = 50f, muscleId = muscleTricepsMiddleTrained.muscle.id, timestamp = now - 2 * dayMillis),
            FatigueLog(id = 3, value = 75f, muscleId = muscleQuadricepsHardTrained.muscle.id, timestamp = now - 4 * dayMillis),

            // Previous week
            FatigueLog(id = 4, value = 60f, muscleId = muscleTricepsMiddleTrained.muscle.id, timestamp = now - weekMillis - dayMillis),

            // Before previous week
            FatigueLog(id = 5, value = 80f, muscleId = muscleQuadricepsHardTrained.muscle.id, timestamp = now - 2 * weekMillis - 2 * dayMillis)
        )
    }
