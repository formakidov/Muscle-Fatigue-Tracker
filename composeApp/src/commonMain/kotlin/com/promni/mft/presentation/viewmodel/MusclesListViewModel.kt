package com.promni.mft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promni.mft.core.Result
import com.promni.mft.core.asResult
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.usecase.GetMuscleFilterUseCase
import com.promni.mft.domain.usecase.GetMuscleInfoUseCase
import com.promni.mft.domain.usecase.SetMuscleFilterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusclesListViewModel(
    getMuscleInfoUseCase: GetMuscleInfoUseCase,
    getMuscleFilterUseCase: GetMuscleFilterUseCase,
    private val setMuscleFilterUseCase: SetMuscleFilterUseCase,
) : ViewModel() {

    val muscleUiState: StateFlow<MuscleUiState> = muscleUiState(getMuscleInfoUseCase)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MuscleUiState.Loading
        )

    val muscleFilter: StateFlow<MuscleFilter> = getMuscleFilterUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MuscleFilter.ALL
        )

    fun setMuscleFilter(filter: MuscleFilter) {
        viewModelScope.launch {
            setMuscleFilterUseCase(filter)
        }
    }
}

private fun muscleUiState(getMuscleInfoUseCase: GetMuscleInfoUseCase): Flow<MuscleUiState> =
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
