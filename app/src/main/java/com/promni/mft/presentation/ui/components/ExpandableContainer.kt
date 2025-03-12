package com.promni.mft.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * A composable that provides a container with expandable content.
 *
 * The container can be expanded or collapsed by clicking on it, or by using a custom expand trigger.
 * When expanded, the `expandedContent` composable is displayed.
 *
 * @param modifier The modifier to be applied to the container.
 * @param isExpanded The initial state of the container. If `true`, the container is expanded.
 * @param onExpandedChange A callback that is invoked when the container is expanded or collapsed.
 * @param expandedContent The composable to be displayed when the container is expanded.
 * @param expandTrigger An optional composable that is used to trigger the expansion or collapse of the container.
 *   If provided, the container is not clickable by default and relies on this trigger for state changes.
 *   The trigger composable receives the current `isExpanded` state and the `onExpandedChange` callback.
 * @param content The main content of the container, displayed initially. It receives the current `isExpanded` state.
 */
@Composable
fun ExpandableContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    isExpanded: Boolean = false,
    onExpandedChange: () -> Unit = {},
    expandedContent:  @Composable () -> Unit,
    expandTrigger: (@Composable (isExpanded: Boolean, onExpandedChange: () -> Unit) -> Unit)? = null,
    content: @Composable (isExpanded: Boolean) -> Unit
) {
    val clickableModifier = expandTrigger?.let { modifier } ?: modifier.clickable { onExpandedChange() }
    Column(
        modifier = clickableModifier
            .padding(contentPadding)
    ) {
        content(isExpanded)

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            expandedContent()
        }

        expandTrigger?.let {
            it(isExpanded, onExpandedChange)
        }
    }
}
