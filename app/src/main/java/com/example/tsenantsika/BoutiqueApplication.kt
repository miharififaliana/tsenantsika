package com.example.tsenantsika

import android.app.Application
import com.example.tsenantsika.di.appModule
import com.example.tsenantsika.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class BoutiqueApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialisation Timber (logs)
        Timber.plant(Timber.DebugTree())

        // Initialisation Koin avec tous les modules
        startKoin {
            androidContext(this@BoutiqueApplication)
            modules(
                appModule,      // Module existant (UI, ViewModels, etc.)
                dataModule      // Nouveau module Data
            )
        }

        Timber.i("✅ Koin initialisé avec succès (DataModule chargé)")
    }
}