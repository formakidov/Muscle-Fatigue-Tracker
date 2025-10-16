package com.promni.mft.domain.util

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MuscleInfoSorterTest {

    private val sorter = MuscleInfoSorter()
    private val now = Clock.System.now().toEpochMilliseconds()

    @Test
    fun `sorts muscles correctly based on expected recovery and order`() {
        // Given
        val muscleInfoList = listOf(
            MuscleInfo(Muscle(4, "Triceps", 1), 0.5f, expectedRecovery = now - 25000L, 10L),
            MuscleInfo(Muscle(3, "Calves", 4), 0.2f, expectedRecovery = now - 26000L, 10L),
            MuscleInfo(Muscle(5, "Hamstrings", 3), 0.3f, expectedRecovery = now - 25000L, 10L),
            MuscleInfo(Muscle(2, "Abs", 2), 0.1f, expectedRecovery = now + 5000L, 10L),
            MuscleInfo(Muscle(1, "Biceps", 0), 0.3f, expectedRecovery = now + 15000L, 10L)
        )

        // Expected order:
        // 1. Ready to train, sorted by order (Calves, Hamstrings, Triceps)
        // 2. In recovery, sorted by soonest recovery time(Biceps, Abs), then by order (Biceps, Abs)
        val expectedOrderList = listOf(
            muscleInfoList[0], // Triceps (order 1)
            muscleInfoList[2], // Hamstrings (order 3)
            muscleInfoList[1], // Calves (order 4)
            muscleInfoList[3], // Abs (Recovers in 5s)
            muscleInfoList[4] // Biceps (Recovers in 15s)
        )

        // When
        val actualSortedList = sorter.sort(muscleInfoList)

        // Then
        assertEquals(expectedOrderList.map { it.muscle.name }, actualSortedList.map { it.muscle.name })
    }
}
