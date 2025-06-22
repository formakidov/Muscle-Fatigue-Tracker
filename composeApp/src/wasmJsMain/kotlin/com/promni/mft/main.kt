package com.promni.mft

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.promni.mft.di.appModules
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin(appDeclaration = {})
    ComposeViewport(document.body!!) {
        App()
    }
}
