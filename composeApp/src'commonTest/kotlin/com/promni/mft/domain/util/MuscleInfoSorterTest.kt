package com.promni.mft.domain.util

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MuscleInfoSorterTest {

    // Instantiate the class under test
    private val sorter = MuscleInfoSorter()
    private val now = Clock.System.now().toEpochMilliseconds()

    @Test
    fun `sorts muscles correctly based on recovery time and name`() {
        // Given
        val muscleInfoList = listOf(
            MuscleInfo(Muscle(4, "Triceps"), 0.5f, expectedRecovery = now - 25000L, 10L), // Ready, name T
            MuscleInfo(Muscle(3, "Calves"), 0.2f, expectedRecovery = now - 26000L, 10L), // Ready, name C
            MuscleInfo(Muscle(5, "Hamstrings"), 0.3f, expectedRecovery = now - 25000L, 10L), // Ready, name H
            MuscleInfo(Muscle(2, "Abs"), 0.1f, expectedRecovery = now + 15000L, 10L),      // In Recovery, 15s
            MuscleInfo(Muscle(1, "Biceps"), 0.3f, expectedRecovery = now + 5000L, 10L),     // In Recovery, 5s
        )
        
        // Expected order:
        // 1. In recovery, sorted by soonest recovery time (Biceps, then Abs)
        // 2. Ready to train, sorted alphabetically (Calves, Hamstrings, Triceps)
        val expectedOrderList = listOf(
            muscleInfoList[4], // Biceps
            muscleInfoList[3], // Abs
            muscleInfoList[1], // Calves
            muscleInfoList[2], // Hamstrings
            muscleInfoList[0]  // Triceps
        )

        // When
        val actualSortedList = sorter.sort(muscleInfoList)

        // Then
        assertEquals(expectedOrderList.map { it.muscle.name }, actualSortedList.map { it.muscle.name })
    }
}
