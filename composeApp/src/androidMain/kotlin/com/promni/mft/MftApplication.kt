package com.promni.mft

import android.app.Application
import com.promni.mft.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MftApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(appDeclaration = {
            androidLogger()
            androidContext(this@MftApplication)
        })
    }
}
