package com.promni.mft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promni.mft.core.Result
import com.promni.mft.core.asResult
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.usecase.GetMuscleFilterUseCase
import com.promni.mft.domain.usecase.GetMusclesInfoUseCase
import com.promni.mft.domain.usecase.SetMuscleFilterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusclesListViewModel(
    getMusclesInfoUseCase: GetMusclesInfoUseCase,
    getMuscleFilterUseCase: GetMuscleFilterUseCase,
    private val setMuscleFilterUseCase: SetMuscleFilterUseCase,
) : ViewModel() {

    val uiState: StateFlow<MusclesListUiState> = muscleUiState(getMusclesInfoUseCase)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MusclesListUiState.Loading
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

private fun muscleUiState(getMusclesInfoUseCase: GetMusclesInfoUseCase): Flow<MusclesListUiState> =
    getMusclesInfoUseCase()
        .asResult()
        .map { result ->
            when (result) {
                is Result.Success -> MusclesListUiState.Success(result.data)
                is Result.Loading -> MusclesListUiState.Loading
                is Result.Error -> MusclesListUiState.Error(result.exception)
            }
        }

sealed interface MusclesListUiState {
    object Loading : MusclesListUiState
    data class Error(val exception: Throwable) : MusclesListUiState
    data class Success(val musclesInfo: List<MuscleInfo>) : MusclesListUiState
}
