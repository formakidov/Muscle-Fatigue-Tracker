package com.promni.mft.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class DataModuleTest : KoinTest {

    @Test
    fun `test DataModule loads`() {
        startKoin {
            modules(dataModule)
        }

        assertNotNull(dataModule, "DataModule should not be null")

        stopKoin()
    }
}
