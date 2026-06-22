package com.example.tsenantsika

import android.app.Application
import com.example.tsenantsika.di.appModule
import org.koin.core.context.startKoin
import timber.log.Timber

class BoutiqueApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            modules(appModule)
        }
    }
}