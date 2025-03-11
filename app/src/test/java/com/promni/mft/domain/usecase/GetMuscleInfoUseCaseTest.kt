package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetMuscleInfoUseCaseTest {

    private val muscleRepository: MuscleRepository = mockk()
    private val getMuscleInfoUseCase = GetMuscleInfoUseCase(muscleRepository)

    @Test
    fun `test muscles order by recovery time and then alphabetically by name`() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val muscleInfoList = listOf(
            MuscleInfo(Muscle(id = 1, name = "Triceps"), fatigue = 0.5f, expectedRecovery = 0L, totalRecoveryTime = 10L),
            MuscleInfo(Muscle(id = 4, name = "Calves"), fatigue = 0.2f, expectedRecovery = 0L, totalRecoveryTime = 10L),
            MuscleInfo(Muscle(id = 2, name = "Biceps"), fatigue = 0.3f, expectedRecovery = now + 5000L, totalRecoveryTime = 10L),
            MuscleInfo(Muscle(id = 3, name = "Abs"), fatigue = 0.1f, expectedRecovery = now + 15000L, totalRecoveryTime = 10L),
        )
        every { muscleRepository.observeMuscles() } returns flowOf(muscleInfoList)

        // When
        val fetchedMuscleInfoList = getMuscleInfoUseCase.invoke().first()

        // Then
        val expectedOrder = listOf("Biceps", "Abs", "Calves", "Triceps")
        val actualOrder = fetchedMuscleInfoList.map { it.muscle.name }
        assertEquals(expectedOrder, actualOrder)
    }

    @Test
    fun `test muscles with empty list`() = runTest {
        // Given
        every { muscleRepository.observeMuscles() } returns flowOf(emptyList())

        // When
        val sortedMuscles = getMuscleInfoUseCase.invoke().first()

        // Then
        assertEquals(emptyList<MuscleInfo>(), sortedMuscles)
    }
}
