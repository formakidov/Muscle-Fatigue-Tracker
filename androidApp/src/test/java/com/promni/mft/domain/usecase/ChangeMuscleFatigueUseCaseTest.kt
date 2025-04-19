package com.promni.mft.domain.usecase

import com.promni.mft.MuscleNotFoundException
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeMuscleFatigueUseCaseTest {

    private lateinit var changeMuscleFatigueUseCase: ChangeMuscleFatigueUseCase
    private val muscleRepository: MuscleRepository = mockk(relaxed = true)
    private val fatigueLogRepository: FatigueLogRepository = mockk(relaxed = true)
    private val expectedRecoveryRepository: ExpectedRecoveryRepository = mockk(relaxed = true)

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
    fun `Fatigue negative value throws exception`() = runTest {
        // Given
        val newValue = -10f // Attempting to decrease fatigue below 0

        // When
        assertThrows<IllegalArgumentException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }

        // Then
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
        coVerify(exactly = 0) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun `Fatigue more than 100 throws exception`() = runTest {
        // Given
        val newValue = 101f // Attempting to increase fatigue beyond 100

        // When
        assertThrows<IllegalArgumentException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }

        // Then
        // Ensure no updates were attempted
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
        coVerify(exactly = 0) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun `Positive amount`() = runTest {
        // Given
        val newValue = 10f

        // When
        changeMuscleFatigueUseCase(muscleId, newValue)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        coVerify(exactly = 1) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
    }

    @Test
    fun `Muscle not in DB`() = runTest {
        // Given
        val newValue = 10f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } throws MuscleNotFoundException(muscleId)

        // Then
        assertThrows<MuscleNotFoundException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }
        // Ensure no updates were attempted
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
        coVerify(exactly = 0) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun `Small amount`() = runTest {
        // Given
        val newValue = 0.00001f

        // When
        changeMuscleFatigueUseCase(muscleId, newValue)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        coVerify(exactly = 1) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
    }
}
