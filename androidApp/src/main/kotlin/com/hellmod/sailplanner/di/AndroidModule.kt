package com.hellmod.sailplanner.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hellmod.sailplanner.data.local.db.SailPlannerDatabase
import com.hellmod.sailplanner.domain.service.LocationService
import com.hellmod.sailplanner.service.AndroidLocationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<SailPlannerDatabase> {
        val driver = AndroidSqliteDriver(
            schema = SailPlannerDatabase.Schema,
            context = androidContext(),
            name = "sail_planner.db"
        )
        SailPlannerDatabase(driver)
    }

    single<LocationService> {
        AndroidLocationService(androidContext())
    }
}
