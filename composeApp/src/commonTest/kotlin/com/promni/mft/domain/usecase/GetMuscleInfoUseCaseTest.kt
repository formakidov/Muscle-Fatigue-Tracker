package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.repository.UserDataRepository
import com.promni.mft.domain.util.MuscleInfoSorter
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMuscleInfoUseCaseTest {

    private val muscleRepository: MuscleRepository = mock()
    private val muscleInfoSorter: MuscleInfoSorter = mock()
    private val userDataRepository: UserDataRepository = mock()

    private val getMusclesInfoUseCase = GetMusclesInfoUseCase(
        muscleRepository,
        userDataRepository,
        muscleInfoSorter
    )

    @Test
    fun `invoke calls repository and sorter, and returns sorter's result`() = runTest {
        // Given
        val muscle1 = Muscle(1, "Biceps")
        val muscle2 = Muscle(2, "Triceps")
        val unsortedMuscleInfoList = listOf(
            MuscleInfo(muscle1, fatigue = 0.5f, expectedRecovery = 0L, totalRecoveryTime = 10L),
            MuscleInfo(muscle2, fatigue = 0.3f, expectedRecovery = 1000L, totalRecoveryTime = 10L)
        )
        val sortedMuscleInfoList = listOf(
            MuscleInfo(muscle2, fatigue = 0.3f, expectedRecovery = 1000L, totalRecoveryTime = 10L),
            MuscleInfo(muscle1, fatigue = 0.5f, expectedRecovery = 0L, totalRecoveryTime = 10L)
        )

        every { userDataRepository.muscleFilter } returns flowOf(MuscleFilter.ALL)
        every { muscleRepository.observeMuscles() } returns flowOf(unsortedMuscleInfoList)
        every { muscleInfoSorter.sort(unsortedMuscleInfoList) } returns sortedMuscleInfoList

        // When
        val result = getMusclesInfoUseCase.invoke().first()

        // Then
        verify { muscleRepository.observeMuscles() }
        verify { muscleInfoSorter.sort(unsortedMuscleInfoList) }
        assertEquals(sortedMuscleInfoList, result)
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        // Given
        every { userDataRepository.muscleFilter } returns flowOf(MuscleFilter.ALL)
        every { muscleRepository.observeMuscles() } returns flowOf(emptyList())
        every { muscleInfoSorter.sort(emptyList()) } returns emptyList()

        // When
        val result = getMusclesInfoUseCase.invoke().first()

        // Then
        verify { muscleRepository.observeMuscles() }
        verify { muscleInfoSorter.sort(emptyList()) }
        assertEquals(emptyList(), result)
    }
}
