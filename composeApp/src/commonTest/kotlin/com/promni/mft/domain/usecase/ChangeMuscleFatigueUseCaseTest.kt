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
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

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
        val newValue = -10f

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            changeMuscleFatigueUseCase.invoke(muscleId, newValue)
        }

        // Then
        verifySuspend(exactly(0)) { muscleRepository.currentTotalRecoveryTime(any()) }
        verifySuspend(exactly(0)) { fatigueLogRepository.addFatigueLog(any(), any()) }
        verifySuspend(exactly(0)) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun invoke_withFatigueValueGreaterThan100_throwsIllegalArgumentException() = runTest {
        // Given
        val newValue = 101f

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }
        
        // Then
        verifySuspend(exactly(0)) { muscleRepository.currentTotalRecoveryTime(any()) }
        verifySuspend(exactly(0)) { fatigueLogRepository.addFatigueLog(any(), any()) }
        verifySuspend(exactly(0)) { expectedRecoveryRepository.setExpectedRecovery(any(), any()) }
    }

    @Test
    fun invoke_muscleNotFoundInDatabase_throwsMuscleNotFoundException() = runTest {
        // Given
        val newValue = 10f
        everySuspend { muscleRepository.currentTotalRecoveryTime(muscleId) } throws MuscleNotFoundException(muscleId)

        // When & Then
        assertFailsWith<MuscleNotFoundException> {
            changeMuscleFatigueUseCase(muscleId, newValue)
        }
        
        // Then
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
        verifySuspend(exactly(1)) { muscleRepository.currentTotalRecoveryTime(muscleId) }
        verifySuspend(exactly(1)) { expectedRecoveryRepository.setExpectedRecovery(muscleId, any()) }
        verifySuspend(exactly(1)) { fatigueLogRepository.addFatigueLog(muscleId, newValue) }
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
