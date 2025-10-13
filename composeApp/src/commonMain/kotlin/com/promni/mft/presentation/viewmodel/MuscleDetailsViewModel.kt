package com.promni.mft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promni.mft.core.Result
import com.promni.mft.core.Result.Success
import com.promni.mft.core.asResult
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.DeleteFatigueLogUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.GetMuscleByIdUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MuscleDetailsViewModel(
    private val muscleId: Long,
    getFatigueLogsUseCase: GetFatigueLogsUseCase,
    getMuscleByIdUseCase: GetMuscleByIdUseCase,
    private val setTotalRecoveryTimeUseCase: SetTotalRecoveryTimeUseCase,
    private val changeFatigueUseCase: ChangeMuscleFatigueUseCase,
    private val deleteFatigueLogUseCase: DeleteFatigueLogUseCase,
) : ViewModel() {

    val uiState: StateFlow<MuscleDetailsUiState> = uiState(
        muscleId = muscleId,
        getFatigueLogsUseCase = getFatigueLogsUseCase,
        getMuscleByIdUseCase = getMuscleByIdUseCase,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MuscleDetailsUiState.Loading
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

    private fun setTotalRecoveryTime(muscleId: Long, newTotalRecoveryTime: Long) {
        viewModelScope.launch {
            setTotalRecoveryTimeUseCase(muscleId, newTotalRecoveryTime)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun uiState(
    muscleId: Long,
    getFatigueLogsUseCase: GetFatigueLogsUseCase,
    getMuscleByIdUseCase: GetMuscleByIdUseCase
): Flow<MuscleDetailsUiState> {
    val muscleInfoFlow = getMuscleByIdUseCase(muscleId)
    val fatigueLogsFlow = getFatigueLogsUseCase(muscleId)

    return combine(muscleInfoFlow, fatigueLogsFlow) { muscleInfo, logs ->
        MuscleDetailsUiState.Success(
            muscleInfo = muscleInfo,
            logs = logs
        )
    }
        .asResult()
        .map { result ->
            when (result) {
                is Success -> result.data
                is Result.Loading -> MuscleDetailsUiState.Loading
                is Result.Error -> MuscleDetailsUiState.Error
            }
        }
}

sealed interface MuscleDetailsUiState {
    object Loading : MuscleDetailsUiState
    object Error : MuscleDetailsUiState
    data class Success(val muscleInfo: MuscleInfo, val logs: List<FatigueLog>) : MuscleDetailsUiState
}
