package com.hellmod.sailplanner

import androidx.compose.ui.window.ComposeUIViewController
import com.hellmod.sailplanner.di.presentationModule
import com.hellmod.sailplanner.presentation.ui.screens.TripListScreen
import com.hellmod.sailplanner.presentation.ui.theme.SailPlannerTheme
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    SailPlannerTheme {
        TripListScreen(onTripClick = {}, onCreateTrip = {})
    }
}
