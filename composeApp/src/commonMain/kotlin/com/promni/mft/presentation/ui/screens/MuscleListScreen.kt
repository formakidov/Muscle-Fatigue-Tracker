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
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.presentation.ui.components.MuscleDetailsBottomSheet
import com.promni.mft.presentation.ui.components.MuscleItem
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.DevicePreviews
import com.promni.mft.presentation.ui.utils.allMuscles
import com.promni.mft.presentation.ui.utils.getWindowSizeClass
import com.promni.mft.presentation.viewmodel.MuscleUiState
import com.promni.mft.presentation.viewmodel.MusclesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MuscleListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusclesListViewModel = koinViewModel()
) {
    val muscleUiState by viewModel.muscleUiState.collectAsStateWithLifecycle()

    MuscleListScreen(
        modifier,
        uiState = muscleUiState,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleListScreen(
    modifier: Modifier,
    uiState: MuscleUiState,
) {
    var selectedMuscleId by remember { mutableStateOf<Long?>(null) }
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
                    MusclesListContent(musclesInfo, onMuscleSelected = {
                        selectedMuscleId = it.muscle.id
                    })
                }

                val selectedMuscle = selectedMuscleId?.let { id ->
                    musclesInfo.find { it.muscle.id == id }
                }

                if (selectedMuscle != null) {
                    MuscleDetailsBottomSheet(
                        muscleInfo = selectedMuscle,
                        sheetState = sheetState,
                        onDismiss = { selectedMuscleId = null }
                    )
                }
            }
        }
    }
}

@Composable
fun MusclesListContent(musclesInfo: List<MuscleInfo>, onMuscleSelected: (muscle: MuscleInfo) -> Unit) {
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
                onClick = { onMuscleSelected(muscleInfo) },
            )
        }
    }
}


@Composable
private fun ThemedMuscleListScreenPreview(
    darkTheme: Boolean,
    uiState: MuscleUiState,
    modifier: Modifier = Modifier
) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface {
            MuscleListScreen(
                modifier = modifier,
                uiState = uiState,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun MuscleListScreenSuccessLightPreview() = ThemedMuscleListScreenPreview(
    darkTheme = false,
    uiState = MuscleUiState.Success(allMuscles),
)

@DevicePreviews
@Composable
private fun MuscleListScreenSuccessDarkPreview() = ThemedMuscleListScreenPreview(
    darkTheme = true,
    uiState = MuscleUiState.Success(allMuscles),
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
