package com.promni.mft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promni.mft.core.Result
import com.promni.mft.core.asResult
import com.promni.mft.data.local.entities.MuscleId

import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.GetMuscleInfoUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusclesListViewModel(
    getMuscleInfoUseCase: GetMuscleInfoUseCase,
    getFatigueLogsUseCase: GetFatigueLogsUseCase,
    private val setTotalRecoveryTimeUseCase: SetTotalRecoveryTimeUseCase,
    private val changeFatigueUseCase: ChangeMuscleFatigueUseCase,
) : ViewModel() {

    private val _selectedMuscleId = MutableStateFlow<MuscleId?>(null)
    val selectedMuscleId: StateFlow<MuscleId?> = _selectedMuscleId.asStateFlow()

    val muscleUiState: StateFlow<MuscleUiState> = muscleUiState(getMuscleInfoUseCase)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MuscleUiState.Loading
        )

    val fatigueLogUiState: StateFlow<FatigueLogUiState> = fatigueLogUiState(_selectedMuscleId, getFatigueLogsUseCase)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FatigueLogUiState.Loading
        )

    fun selectMuscle(muscleId: MuscleId) {
        _selectedMuscleId.value = muscleId
    }

    fun setFatigue(muscleInfo: MuscleInfo, newValue: Float) {
        viewModelScope.launch {
            changeFatigueUseCase(muscleInfo.muscle.id, newValue)
        }
    }

    fun setTotalRecoveryTime(muscleId: MuscleId, newTotalRecoveryTime: Long) {
        viewModelScope.launch {
            setTotalRecoveryTimeUseCase(muscleId, newTotalRecoveryTime)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun MusclesListViewModel.fatigueLogUiState(
    selectedMuscleId: MutableStateFlow<MuscleId?>,
    getFatigueLogsUseCase: GetFatigueLogsUseCase
): Flow<FatigueLogUiState> = selectedMuscleId
    .flatMapLatest { muscleId ->
        if (muscleId != null) {
            getFatigueLogsUseCase(muscleId)
                .asResult()
        } else {
            flowOf(Result.Success(emptyList()))
        }
    }
    .map { result ->
        when (result) {
            is Result.Success -> FatigueLogUiState.Success(result.data)
            is Result.Loading -> FatigueLogUiState.Loading
            is Result.Error -> FatigueLogUiState.Error
        }
    }

private fun MusclesListViewModel.muscleUiState(getMuscleInfoUseCase: GetMuscleInfoUseCase): Flow<MuscleUiState> =
    getMuscleInfoUseCase()
        .asResult()
        .map { result ->
            when (result) {
                is Result.Success -> MuscleUiState.Success(result.data)
                is Result.Loading -> MuscleUiState.Loading
                is Result.Error -> MuscleUiState.Error(result.exception)
            }
        }

sealed interface MuscleUiState {
    object Loading : MuscleUiState
    data class Error(val exception: Throwable) : MuscleUiState
    data class Success(val musclesInfo: List<MuscleInfo>) : MuscleUiState
}

sealed interface FatigueLogUiState {
    object Loading : FatigueLogUiState
    object Error : FatigueLogUiState
    data class Success(val logs: List<FatigueLog>) : FatigueLogUiState
}
