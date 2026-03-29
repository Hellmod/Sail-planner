package com.hellmod.sailplanner

import com.hellmod.sailplanner.di.presentationModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(presentationModule)
        // iOS-specific modules (DB driver, location service) go here
    }
}
