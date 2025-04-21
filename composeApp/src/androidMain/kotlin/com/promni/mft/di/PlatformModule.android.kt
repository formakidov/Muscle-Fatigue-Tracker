package com.promni.mft.di

import com.promni.mft.AndroidPlatform
import com.promni.mft.Platform
import com.promni.mft.data.local.AppDatabase
import com.promni.mft.data.local.getDatabase
import com.promni.mft.data.local.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Platform> { AndroidPlatform() }
    single<AppDatabase> { getDatabase(getDatabaseBuilder(androidContext())) }
}
