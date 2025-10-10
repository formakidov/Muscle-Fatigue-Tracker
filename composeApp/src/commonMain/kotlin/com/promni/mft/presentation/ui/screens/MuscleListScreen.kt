package com.promni.mft.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.presentation.ui.components.MuscleDetailsBottomSheet
import com.promni.mft.presentation.ui.components.MuscleItem
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.DevicePreviews
import com.promni.mft.presentation.ui.utils.allMuscles
import com.promni.mft.presentation.ui.utils.getWindowSizeClass
import com.promni.mft.presentation.viewmodel.FatigueLogUiState
import com.promni.mft.presentation.viewmodel.MuscleUiState
import com.promni.mft.presentation.viewmodel.MusclesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MuscleListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusclesListViewModel = koinViewModel()
) {
    val muscleUiState by viewModel.muscleUiState.collectAsStateWithLifecycle()
    val fatigueLogUiState by viewModel.fatigueLogUiState.collectAsStateWithLifecycle()
    val selectedMuscleId by viewModel.selectedMuscleId.collectAsStateWithLifecycle()

    MuscleListScreen(
        modifier,
        uiState = muscleUiState,
        fatigueLogUiState = fatigueLogUiState,
        selectedMuscleId = selectedMuscleId,
        onMuscleSelected = viewModel::selectMuscle,
        onFatigueChanged = viewModel::setFatigue,
        onRecoveryPeriodChanged = viewModel::setRecoveryPeriod
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleListScreen(
    modifier: Modifier,
    uiState: MuscleUiState,
    fatigueLogUiState: FatigueLogUiState = FatigueLogUiState.Loading,
    selectedMuscleId: MuscleId?,
    onMuscleSelected: (MuscleId) -> Unit,
    onFatigueChanged: (MuscleInfo, newValue: Float) -> Unit,
    onRecoveryPeriodChanged: (MuscleInfo, Int) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Box(modifier = modifier) {
        when (uiState) {
            is MuscleUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MuscleUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error loading muscles")
                    Text(uiState.exception.toString())
                }
            }

            is MuscleUiState.Success -> {
                val musclesInfo = uiState.musclesInfo
                if (musclesInfo.isEmpty()) {
                    Text("No muscles found")
                } else {
                    MusclesContent(musclesInfo, onMuscleSelected = {
                        onMuscleSelected(it)
                        showBottomSheet = true
                    })
                }

                val muscleInfo = musclesInfo.find { it.muscle.id == selectedMuscleId }
                if (showBottomSheet && muscleInfo != null) {
                    val logs = when (fatigueLogUiState) {
                        is FatigueLogUiState.Success -> fatigueLogUiState.logs
                        else -> emptyList()
                    }

                    MuscleDetailsBottomSheet(
                        muscleInfo = muscleInfo,
                        sheetState = sheetState,
                        logs = logs,
                        onDismiss = { showBottomSheet = false },
                        onFatigueChanged = { muscleInfo, newValue ->
                            onFatigueChanged(muscleInfo, newValue)
                            showBottomSheet = false
                        },
                        onRecoveryPeriodChanged = onRecoveryPeriodChanged,
                        onDeleteLog = { }, // todo
                        onUpdateLog = { } // todo
                    )
                }
            }
        }
    }
}

@Composable
fun MusclesContent(musclesInfo: List<MuscleInfo>, onMuscleSelected: (id: MuscleId) -> Unit) {
    val windowSizeClass = getWindowSizeClass()
    val widthSizeClass = windowSizeClass.widthSizeClass
    val contentPadding = PaddingValues(16.dp)
    val itemSpacing = 12.dp

    val columnsCount = when (widthSizeClass) {
        WindowWidthSizeClass.Medium -> 2
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnsCount),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(musclesInfo, key = { it.muscle.id }) { muscleInfo ->
            MuscleItem(
                muscleInfo = muscleInfo,
                onClick = { onMuscleSelected(muscleInfo.muscle.id) },
            )
        }
    }
}


@Composable
private fun ThemedMuscleListScreenPreview(
    darkTheme: Boolean,
    uiState: MuscleUiState,
    fatigueLogUiState: FatigueLogUiState = FatigueLogUiState.Loading,
    modifier: Modifier = Modifier
) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface {
            MuscleListScreen(
                modifier = modifier,
                uiState = uiState,
                fatigueLogUiState = fatigueLogUiState,
                selectedMuscleId = null,
                onMuscleSelected = {},
                onFatigueChanged = { _, _ -> },
                onRecoveryPeriodChanged = { _, _ -> }
            )
        }
    }
}

@DevicePreviews
@Composable
private fun MuscleListScreenSuccessLightPreview() = ThemedMuscleListScreenPreview(
    darkTheme = false,
    uiState = MuscleUiState.Success(allMuscles),
    fatigueLogUiState = FatigueLogUiState.Success(emptyList())
)

@DevicePreviews
@Composable
private fun MuscleListScreenSuccessDarkPreview() = ThemedMuscleListScreenPreview(
    darkTheme = true,
    uiState = MuscleUiState.Success(allMuscles),
    fatigueLogUiState = FatigueLogUiState.Success(emptyList())
)

@DevicePreviews
@Composable
private fun MuscleListScreenLoadingLightPreview() =
    ThemedMuscleListScreenPreview(darkTheme = false, uiState = MuscleUiState.Loading)

@DevicePreviews
@Composable
private fun MuscleListScreenLoadingDarkPreview() =
    ThemedMuscleListScreenPreview(darkTheme = true, uiState = MuscleUiState.Loading)

@DevicePreviews
@Composable
private fun MuscleListScreenErrorLightPreview() = ThemedMuscleListScreenPreview(
    darkTheme = false, uiState = MuscleUiState.Error(Exception("Preview Error"))
)

@DevicePreviews
@Composable
private fun MuscleListScreenErrorDarkPreview() = ThemedMuscleListScreenPreview(
    darkTheme = true, uiState = MuscleUiState.Error(Exception("Preview Error"))
)
