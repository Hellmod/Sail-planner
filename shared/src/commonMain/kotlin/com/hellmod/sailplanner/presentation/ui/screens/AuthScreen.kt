package com.hellmod.sailplanner.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hellmod.sailplanner.presentation.auth.AuthIntent
import com.hellmod.sailplanner.presentation.auth.AuthViewModel
import com.hellmod.sailplanner.presentation.ui.theme.DeepNavy
import com.hellmod.sailplanner.presentation.ui.theme.OceanBlue
import com.hellmod.sailplanner.presentation.ui.theme.SeaFoam
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, OceanBlue, SeaFoam)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo / icon placeholder
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83C\uDFF4\u200D\u2620\uFE0F", style = MaterialTheme.typography.displayLarge)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Sail Planner",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Text(
                text = "Plan your sailing adventure",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                // Google Sign-In
                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF333333)
                    )
                ) {
                    Text("Continue with Google", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.height(12.dp))

                // Apple Sign-In
                Button(
                    onClick = onAppleSignIn,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(" Continue with Apple", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.height(24.dp))

                TextButton(onClick = { viewModel.dispatch(AuthIntent.SignInAsGuest) }) {
                    Text(
                        "Continue as Guest",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            state.error?.let { error ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
