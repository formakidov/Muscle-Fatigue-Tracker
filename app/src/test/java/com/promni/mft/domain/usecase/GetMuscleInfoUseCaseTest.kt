package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.MuscleInfoSorter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMuscleInfoUseCaseTest {

    private val muscleRepository: MuscleRepository = mockk()
    private val muscleInfoSorter: MuscleInfoSorter = mockk()

    private lateinit var getMuscleInfoUseCase: GetMuscleInfoUseCase

    @BeforeEach
    fun setup() {
        getMuscleInfoUseCase = GetMuscleInfoUseCase(muscleRepository, muscleInfoSorter)
    }

    @Test
    fun `invoke calls repository and sorter, and returns sorter's result`() = runTest {
        // Given
        val unsortedMuscleInfoList = listOf(
            MuscleInfo(mockk(), fatigue = 0.5f, expectedRecovery = 0L, totalRecoveryTime = 10L),
            MuscleInfo(mockk(), fatigue = 0.3f, expectedRecovery = 1000L, totalRecoveryTime = 10L)
        )
        val sortedMuscleInfoList = listOf( // This could be anything, we don't care, sorting logic is tested elsewhere
            MuscleInfo(mockk(), fatigue = 0.1f, expectedRecovery = 2000L, totalRecoveryTime = 5L),
            MuscleInfo(mockk(), fatigue = 0.8f, expectedRecovery = 500L, totalRecoveryTime = 20L)
        )

        every { muscleRepository.observeMuscles() } returns flowOf(unsortedMuscleInfoList)
        every { muscleInfoSorter.sort(unsortedMuscleInfoList) } returns sortedMuscleInfoList

        // When
        val result = getMuscleInfoUseCase.invoke().first()

        // Then
        verify(exactly = 1) { muscleRepository.observeMuscles() }
        verify(exactly = 1) { muscleInfoSorter.sort(unsortedMuscleInfoList) } // Verify it's called with the unsorted list
        assertEquals(sortedMuscleInfoList, result) // Verify the sorter's result is returned
        confirmVerified(muscleRepository, muscleInfoSorter)
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        // Given
        every { muscleRepository.observeMuscles() } returns flowOf(emptyList())
        every { muscleInfoSorter.sort(emptyList()) } returns emptyList()

        // When
        val result = getMuscleInfoUseCase.invoke().first()

        // Then
        verify(exactly = 1) { muscleRepository.observeMuscles() }
        verify(exactly = 1) { muscleInfoSorter.sort(emptyList()) } // Verify sorter is called
        assertEquals(emptyList<MuscleInfo>(), result)
        confirmVerified(muscleRepository, muscleInfoSorter)
    }
}
