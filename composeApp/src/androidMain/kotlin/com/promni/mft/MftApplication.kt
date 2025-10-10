package com.promni.mft

import android.app.Application
import com.promni.mft.data.local.initDataStore
import com.promni.mft.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDataStore(this)
        initKoin {
            androidLogger()
            androidContext(this@MftApplication)
        }
    }
}
