package com.hellmod.sailplanner.di

import com.hellmod.sailplanner.presentation.auth.AuthViewModel
import com.hellmod.sailplanner.presentation.expenses.ExpenseViewModel
import com.hellmod.sailplanner.presentation.photos.PhotoViewModel
import com.hellmod.sailplanner.presentation.route.RouteViewModel
import com.hellmod.sailplanner.presentation.shopping.ShoppingViewModel
import com.hellmod.sailplanner.presentation.trips.TripListViewModel
import com.hellmod.sailplanner.presentation.watch.WatchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { TripListViewModel(get()) }
    viewModel { ShoppingViewModel(get()) }
    viewModel { WatchViewModel(get()) }
    viewModel { ExpenseViewModel(get()) }
    viewModel { PhotoViewModel(get()) }
    viewModel { RouteViewModel(get(), get()) }
}
