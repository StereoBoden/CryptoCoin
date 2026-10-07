package com.jbappz.cryptocoin

import android.app.Application
import com.jbappz.cryptocoin.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CryptoCoinApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CryptoCoinApplication)
        }
    }
}