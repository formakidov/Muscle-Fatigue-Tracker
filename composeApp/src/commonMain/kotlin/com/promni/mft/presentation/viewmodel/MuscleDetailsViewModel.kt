package com.promni.mft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promni.mft.core.Result
import com.promni.mft.core.asResult
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.DeleteFatigueLogUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MuscleDetailsViewModel(
    private val muscleId: MuscleId,
    getFatigueLogsUseCase: GetFatigueLogsUseCase,
    private val setTotalRecoveryTimeUseCase: SetTotalRecoveryTimeUseCase,
    private val changeFatigueUseCase: ChangeMuscleFatigueUseCase,
    private val deleteFatigueLogUseCase: DeleteFatigueLogUseCase,
) : ViewModel() {

    val fatigueLogUiState: StateFlow<FatigueLogUiState> = fatigueLogUiState(muscleId, getFatigueLogsUseCase)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FatigueLogUiState.Loading
        )

    fun setFatigue(newValue: Float) {
        viewModelScope.launch {
            changeFatigueUseCase(muscleId, newValue)
        }
    }

    fun setRecoveryPeriod(days: Int) {
        val newTotalRecoveryTime = days * 24 * 60 * 60 * 1000L
        setTotalRecoveryTime(muscleId, newTotalRecoveryTime)
    }

    fun deleteLog(date: LocalDate) {
        viewModelScope.launch {
            deleteFatigueLogUseCase(muscleId, date)
        }
    }

    private fun setTotalRecoveryTime(muscleId: MuscleId, newTotalRecoveryTime: Recovery) {
        viewModelScope.launch {
            setTotalRecoveryTimeUseCase(muscleId, newTotalRecoveryTime)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun fatigueLogUiState(
    muscleId: MuscleId,
    getFatigueLogsUseCase: GetFatigueLogsUseCase
): Flow<FatigueLogUiState> =
    getFatigueLogsUseCase(muscleId)
        .asResult()
        .map { result ->
            when (result) {
                is Result.Success -> FatigueLogUiState.Success(result.data)
                is Result.Loading -> FatigueLogUiState.Loading
                is Result.Error -> FatigueLogUiState.Error
            }
        }

sealed interface FatigueLogUiState {
    object Loading : FatigueLogUiState
    object Error : FatigueLogUiState
    data class Success(val logs: List<FatigueLog>) : FatigueLogUiState
}
