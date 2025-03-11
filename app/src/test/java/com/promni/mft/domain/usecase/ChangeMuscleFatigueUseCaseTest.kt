package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.domain.MuscleNotFoundException
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeMuscleFatigueUseCaseTest {

    private lateinit var changeMuscleFatigueUseCase: ChangeMuscleFatigueUseCase
    private val muscleRepository: MuscleRepository = mockk()
    private val fatigueLogRepository: FatigueLogRepository = mockk(relaxed = true)
    private val expectedRecoveryRepository: ExpectedRecoveryRepository = mockk(relaxed = true)

    private val muscleId: Long = 1L
    private val defaultTotalRecoveryTime = 4 * 24 * 60 * 60 * 1000L

    @BeforeEach
    fun setup() {
        changeMuscleFatigueUseCase = ChangeMuscleFatigueUseCase(
            muscleRepository,
            fatigueLogRepository,
            expectedRecoveryRepository
        )
    }

    @Test
    fun `Zero change amount does not add log entry`() = runBlocking {
        // Given
        val changeAmount = 0f // No change in fatigue

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
    }

    @Test
    fun `Zero change amount does not update expected recovery`() = runBlocking {
        // Given
        val changeAmount = 0f // No change in fatigue

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 0) { expectedRecoveryRepository.storeExpectedRecovery(muscleId, any(), any()) }
    }

    @Test
    fun `Fatigue at zero does not go below zero`() = runBlocking {
        // Given
        val changeAmount = -10f // Attempting to decrease fatigue below 0
        val totalRecoveryTime = defaultTotalRecoveryTime
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns totalRecoveryTime
        coEvery { expectedRecoveryRepository.fetchExpectedRecovery(muscleId) } returns null

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
        coVerify(exactly = 0) { expectedRecoveryRepository.storeExpectedRecovery(any(), any(), any()) }
    }

    @Test
    fun `Fatigue at max`() = runBlocking {
        // Given
        val changeAmount = 101f // Attempting to increase fatigue beyond 100
        val totalRecoveryTime = defaultTotalRecoveryTime
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns totalRecoveryTime
        coEvery { expectedRecoveryRepository.fetchExpectedRecovery(muscleId) } returns null

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 1) { expectedRecoveryRepository.storeExpectedRecovery(muscleId, any(), 100f) }
    }

    @Test
    fun `Change amount less than 0`() = runBlocking {
        // Given
        val changeAmount = -10f
        val totalRecoveryTime = defaultTotalRecoveryTime
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns totalRecoveryTime
        coEvery { expectedRecoveryRepository.fetchExpectedRecovery(muscleId) } returns
                ExpectedRecoveryEntity(muscleId, System.currentTimeMillis() + totalRecoveryTime, 0) // Fatigue is 100

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, -10f) }
        coVerify(exactly = 1) { expectedRecoveryRepository.storeExpectedRecovery(muscleId, totalRecoveryTime, 90f) }
    }


    @Test
    fun `Positive change amount`() = runBlocking {
        // Given
        val changeAmount = 10f
        val totalRecoveryTime = defaultTotalRecoveryTime
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns totalRecoveryTime
        coEvery { expectedRecoveryRepository.fetchExpectedRecovery(muscleId) } returns null // Fatigue is 0

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, 10f) }
        coVerify(exactly = 1) { expectedRecoveryRepository.storeExpectedRecovery(muscleId, totalRecoveryTime, 10f) }
    }

    @Test
    fun `Muscle not in DB`() = runBlocking {
        // Given
        val changeAmount = 10f
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } throws MuscleNotFoundException(muscleId)

        // When & Then
        assertThrows<MuscleNotFoundException> {
            runBlocking { changeMuscleFatigueUseCase(muscleId, changeAmount) }
        }

        // Ensure no updates were attempted
        coVerify(exactly = 0) { fatigueLogRepository.addFatigueLog(any(), any()) }
        coVerify(exactly = 0) { expectedRecoveryRepository.storeExpectedRecovery(any(), any(), any()) }
    }

    @Test
    fun `Small change amount`() = runBlocking {
        // Given
        val changeAmount = 0.0001f
        val totalRecoveryTime = defaultTotalRecoveryTime
        coEvery { muscleRepository.currentTotalRecoveryTime(muscleId) } returns totalRecoveryTime
        coEvery { expectedRecoveryRepository.fetchExpectedRecovery(muscleId) } returns null

        // When
        changeMuscleFatigueUseCase(muscleId, changeAmount)

        // Then
        coVerify(exactly = 1) { fatigueLogRepository.addFatigueLog(muscleId, changeAmount) }
        coVerify(exactly = 1) { expectedRecoveryRepository.storeExpectedRecovery(muscleId, totalRecoveryTime, changeAmount) }
    }
}
