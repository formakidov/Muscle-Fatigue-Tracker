package com.promni.mft.domain.usecase

import com.promni.mft.domain.MuscleNotFoundException
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.atMost
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

class ChangeMuscleFatigueUseCaseTest {

    private val muscleRepository: MuscleRepository = mock()
    private val fatigueLogRepository: FatigueLogRepository = mock()
    private val expectedRecoveryRepository: ExpectedRecoveryRepository = mock()

    private val changeMuscleFatigueUseCase = ChangeMuscleFatigueUseCase(
        muscleRepository,
        fatigueLogRepository,
        expectedRecoveryRepository
    )

    private val muscleId: Long = 1L

    @Test
    fun invoke_withNegativeFatigueValue_throwsIllegalArgumentException() = runTest {
        // Given
        val newValue = -10f // Attempting to decrease fatigue below 0

        // When
        try {
            changeMuscleFatigueUseCase.invoke(muscleId, newValue)
            fail("Expected IllegalArgumentException was not thrown.")
        } catch (e: IllegalArgumentException) {
            // Exception was thrown as expected
        }

        // Then
        // Ensure no updates were attempted
        verifySuspend(atMost(0)) { muscleRepository.currentTotalRecoveryTime(any()) }
        verifySuspend(atMost(0)) { fatigueLogRepository.addFatigueLog(any(), any()) }
        verifySuspend(atMost(0)) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun invoke_withFatigueValueGreaterThan100_throwsIllegalArgumentException() = runTest {
        // Given
        val newValue = 101f // Attempting to increase fatigue beyond 100

        // When
        try {
            changeMuscleFatigueUseCase(muscleId, newValue)
            fail("Expected IllegalArgumentException was not thrown.")
        } catch (e: IllegalArgumentException) {
            // Exception was thrown as expected
        }

        // Then
        // Ensure no updates were attempted
        verifySuspend(atMost(0)) { muscleRepository.currentTotalRecoveryTime(any()) }
        verifySuspend(atMost(0)) { fatigueLogRepository.addFatigueLog(any(), any()) }
        verifySuspend(atMost(0)) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun invoke_muscleNotFoundInDatabase_throwsMuscleNotFoundException() = runTest {
        // Given
        val newValue = 10f
        everySuspend { muscleRepository.currentTotalRecoveryTime(muscleId) } throws MuscleNotFoundException(muscleId)

        // When
        try {
            changeMuscleFatigueUseCase(muscleId, newValue)
            fail("Expected MuscleNotFoundException was not thrown.")
        } catch (e: MuscleNotFoundException) {
            // Exception was thrown as expected
        }
        
        // Then
        // We can verify the one call that was made
        verifySuspend(exactly(1)) { muscleRepository.currentTotalRecoveryTime(muscleId) }
    }

    @Test
    fun invoke_withValidPositiveFatigueValue_updatesRepositories() = runTest {
        // Given
        val newValue = 10f
        everySuspend { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        everySuspend { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } returns Unit
        everySuspend { fatigueLogRepository.addFatigueLog(muscleId, newValue) } returns Unit

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        verifySuspend(exactly(1)) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        verifySuspend(exactly(1)) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
    }

    // This test is redundant with the one above, but shown for completeness
    @Test
    fun invoke_withSmallPositiveFatigueValue_updatesRepositories() = runTest {
        // Given
        val newValue = 0.00001f
        everySuspend { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        everySuspend { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } returns Unit
        everySuspend { fatigueLogRepository.addFatigueLog(muscleId, newValue) } returns Unit

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        verifySuspend(exactly(1)) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
        verifySuspend(exactly(1)) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
    }
    
    @Test
    fun updatesFatigueAndExpectedRecovery_whenZeroAmount() = runTest {
        // Given
        val newValue = 0f
        everySuspend { muscleRepository.currentTotalRecoveryTime(muscleId) } returns 1000L
        everySuspend { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) } returns Unit
        everySuspend { fatigueLogRepository.addFatigueLog(muscleId, newValue) } returns Unit

        // When
        changeMuscleFatigueUseCase.invoke(muscleId, newValue)

        // Then
        verifySuspend(exactly(1)) { muscleRepository.currentTotalRecoveryTime(muscleId) }
        verifySuspend(exactly(1)) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        verifySuspend(exactly(1)) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
    }
}
