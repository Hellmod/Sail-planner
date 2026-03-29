package com.hellmod.sailplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hellmod.sailplanner.presentation.ui.screens.AuthScreen
import com.hellmod.sailplanner.presentation.ui.screens.TripDashboardScreen
import com.hellmod.sailplanner.presentation.ui.screens.TripListScreen
import com.hellmod.sailplanner.presentation.ui.theme.SailPlannerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SailPlannerTheme {
                // Simplified navigation scaffold –
                // Replace with Decompose RootComponent for full stack navigation
                var screen by remember { mutableStateOf<Screen>(Screen.Auth) }

                when (val s = screen) {
                    Screen.Auth -> AuthScreen(
                        onSignedIn = { screen = Screen.TripList },
                        onGoogleSignIn = { startGoogleSignIn() },
                        onAppleSignIn = { /* Apple Sign-in only on iOS */ }
                    )
                    Screen.TripList -> TripListScreen(
                        onTripClick = { id -> screen = Screen.TripDashboard(id) },
                        onCreateTrip = { /* TODO: open create trip sheet */ }
                    )
                    is Screen.TripDashboard -> TripDashboardScreen(
                        tripId = s.tripId,
                        tripName = "My Voyage",
                        onBack = { screen = Screen.TripList },
                        onShopping = { screen = Screen.Shopping(s.tripId) },
                        onWatches = { screen = Screen.Watches(s.tripId) },
                        onRoute = { screen = Screen.Route(s.tripId) },
                        onPhotos = { screen = Screen.Photos(s.tripId) },
                        onExpenses = { screen = Screen.Expenses(s.tripId) },
                        onCollage = { screen = Screen.Collage(s.tripId) }
                    )
                    is Screen.Shopping -> com.hellmod.sailplanner.presentation.ui.screens.ShoppingScreen(
                        tripId = s.tripId,
                        currentUserId = "me", // TODO: get from auth
                        onBack = { screen = Screen.TripDashboard(s.tripId) }
                    )
                    is Screen.Expenses -> com.hellmod.sailplanner.presentation.ui.screens.ExpensesScreen(
                        tripId = s.tripId,
                        onBack = { screen = Screen.TripDashboard(s.tripId) }
                    )
                    else -> TripListScreen(
                        onTripClick = { id -> screen = Screen.TripDashboard(id) },
                        onCreateTrip = {}
                    )
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        // TODO: Implement Google Sign-In using Credential Manager API
        // val credentialManager = CredentialManager.create(this)
        // val googleIdOption = GetGoogleIdOption.Builder()
        //     .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        //     .build()
        // Launch coroutine and call credentialManager.getCredential(...)
    }
}

sealed interface Screen {
    data object Auth : Screen
    data object TripList : Screen
    data class TripDashboard(val tripId: String) : Screen
    data class Shopping(val tripId: String) : Screen
    data class Watches(val tripId: String) : Screen
    data class Route(val tripId: String) : Screen
    data class Photos(val tripId: String) : Screen
    data class Expenses(val tripId: String) : Screen
    data class Collage(val tripId: String) : Screen
}
