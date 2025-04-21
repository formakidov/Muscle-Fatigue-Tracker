package com.promni.mft

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.promni.mft.presentation.ui.screens.MuscleListScreen
import com.promni.mft.presentation.ui.theme.AppTheme

@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MuscleListScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
