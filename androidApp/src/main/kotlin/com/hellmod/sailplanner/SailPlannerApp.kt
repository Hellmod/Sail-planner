package com.hellmod.sailplanner

import android.app.Application
import com.hellmod.sailplanner.di.androidModule
import com.hellmod.sailplanner.di.presentationModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SailPlannerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize logging
        Napier.base(DebugAntilog())

        // Initialize Koin DI
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@SailPlannerApp)
            modules(androidModule, presentationModule)
        }
    }
}
