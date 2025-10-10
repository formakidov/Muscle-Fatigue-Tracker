package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.promni.mft.domain.repository.MuscleFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSegmentedButton(
    modifier: Modifier = Modifier,
    selectedFilter: MuscleFilter,
    onFilterSelected: (MuscleFilter) -> Unit
) {
    val options = MuscleFilter.entries
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        inactiveContainerColor = MaterialTheme.colorScheme.surface,
        inactiveContentColor = MaterialTheme.colorScheme.onSurface,
        activeBorderColor = MaterialTheme.colorScheme.outline,
        inactiveBorderColor = MaterialTheme.colorScheme.outline
    )

    SingleChoiceSegmentedButtonRow(modifier = modifier.height(IntrinsicSize.Min)) {
        options.forEachIndexed { index, filter ->
            SegmentedButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onFilterSelected(filter) },
                selected = filter == selectedFilter,
                label = {
                    Text(
                        text = filter.toDisplayString(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = colors
            )
        }
    }
}

private fun MuscleFilter.toDisplayString(): String {
    return when (this) {
        MuscleFilter.ALL -> "All"
        MuscleFilter.IN_RECOVERY -> "In Recovery"
        MuscleFilter.READY_TO_TRAIN -> "Ready"
    }
}
