package com.promni.mft

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.promni.mft.di.appModules
import org.koin.core.context.startKoin

fun main() = application {
    startKoin { modules(appModules()) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Muscle Fatigue Tracker",
    ) {
        App(
            darkTheme = isSystemInDarkTheme(),
            dynamicColor = false,
        )
    }
}
