package com.promni.mft.domain.util

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.usecase.GetMuscleInfoUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MuscleInfoSorterTest {
    private val now = System.currentTimeMillis()
    private val muscleInfoList = listOf(
        MuscleInfo(Muscle(4, "Triceps"), 0.5f, expectedRecovery = now - 25000L, 10L),
        MuscleInfo(Muscle(3, "Calves"), 0.2f, expectedRecovery = now - 26000L, 10L),
        MuscleInfo(Muscle(5, "Hamstrings"), 0.3f, expectedRecovery = now - 25000L, 10L),
        MuscleInfo(Muscle(2, "Abs"), 0.1f, expectedRecovery = now + 15000L, 10L),
        MuscleInfo(Muscle(1, "Biceps"), 0.3f, expectedRecovery = now + 5000L, 10L),
    )
    private val muscleRepository = mockk<MuscleRepository>()
    private val expectedOrder = listOf("Biceps", "Abs", "Calves", "Hamstrings", "Triceps")
    private val muscleInfoSorter = MuscleInfoSorter()
    private val getMuscleInfoUseCase = GetMuscleInfoUseCase(muscleRepository, muscleInfoSorter)

    @Test
    fun `test muscles with expectedRecovery less than now order by name, expectedRecovery greater than now sort by recovery then by name`() =
        runTest {
            // Given
            every { muscleRepository.observeMuscles() } returns flowOf(muscleInfoList)

            // When
            val fetchedMuscleInfoList = getMuscleInfoUseCase.invoke().first()

            // Then
            val actualOrder = fetchedMuscleInfoList.map { it.muscle.name }
            assertEquals(expectedOrder, actualOrder)
        }

}
