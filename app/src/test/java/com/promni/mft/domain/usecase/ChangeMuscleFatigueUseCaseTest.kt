package com.promni.mft.domain.usecase

import com.promni.mft.domain.MuscleNotFoundException
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import io.mockk.Runs
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeMuscleFatigueUseCaseTest {

    private val muscleRepository: MuscleRepository = mockk()
    private val fatigueLogRepository: FatigueLogRepository = mockk()
    private val expectedRecoveryRepository: ExpectedRecoveryRepository = mockk()

    private lateinit var changeMuscleFatigueUseCase: ChangeMuscleFatigueUseCase

    private val muscleId: Long = 1L

    @BeforeEach
    fun setup() {
        changeMuscleFatigueUseCase = ChangeMuscleFatigueUseCase(
            muscleRepository,
            fatigueLogRepository,
            expectedRecoveryRepository
        )
    }

    @Test
    fun invoke_withNegativeFatigueValue_throwsIllegalArgumentException() = runTest {
        // Given
        val newValue = -10f // Attempting to decrease fatigue below 0

        // Then
        // Ensure no updates were attempted
        verify { listOf(muscleRepository, fatigueLogRepository, expectedRecoveryRepository) wasNot called }

        assertThrows<IllegalArgumentException> {
            changeMuscleFatigueUseCase.invoke(muscleId, newValue)
        }
    }

    @Test
    fun invoke_withFatigueValueGreaterThan100_throwsIllegalArgumentException() = runTest {
        // Given
        val newValue = 101f // Attempting to increase fatigue beyond 100

        // Then
        // Ensure no updates were attempted
        verify { listOf(muscleRepository, fatigueLogRepository, expectedRecoveryRepository) wasNot called }

        // When
        assertThrows<IllegalArgumentException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }
    }

    @Test
    fun invoke_muscleNotFoundInDatabase_throwsMuscleNotFoundException() = runTest {
        // Given
        val newValue = 10f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } throws MuscleNotFoundException(muscleId)

        // Then
        // Ensure no updates were attempted
        verify { listOf(muscleRepository, fatigueLogRepository, expectedRecoveryRepository) wasNot called }

        assertThrows<MuscleNotFoundException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }
    }

    @Test
    fun invoke_withValidPositiveFatigueValue_updatesRepositories() = runTest {
        // Given
        val newValue = 10f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        coEvery { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } just Runs
        coEvery { fatigueLogRepository.addFatigueLog(muscleId, newValue) } just Runs

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        coVerify(exactly = 1) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        confirmVerified(fatigueLogRepository, expectedRecoveryRepository)
    }

    @Test
    fun invoke_withSmallPositiveFatigueValue_updatesRepositories() = runTest {
        // Given
        val newValue = 0.00001f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        coEvery { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } just Runs
        coEvery { fatigueLogRepository.addFatigueLog(muscleId, newValue) } just Runs

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        coVerify(exactly = 1) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        confirmVerified(fatigueLogRepository, expectedRecoveryRepository)
    }

    @Test
    fun updatesFatigueAndExpectedRecovery_whenZeroAmount() = runTest {
        // Given
        val newValue = 0f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        coEvery { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } just Runs
        coEvery { fatigueLogRepository.addFatigueLog(muscleId, newValue) } just Runs

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        coVerify(exactly = 1) { muscleRepository.currentTotalRecoveryTime(muscleId) }
        coVerify(exactly = 1) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        confirmVerified(muscleRepository, fatigueLogRepository, expectedRecoveryRepository)
    }
}
