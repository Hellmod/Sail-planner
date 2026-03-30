package com.hellmod.sailplanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hellmod.sailplanner.domain.model.Trip
import com.hellmod.sailplanner.domain.model.TripStatus
import com.hellmod.sailplanner.presentation.trips.TripListIntent
import com.hellmod.sailplanner.presentation.trips.TripListViewModel
import com.hellmod.sailplanner.presentation.ui.theme.OceanBlue
import com.hellmod.sailplanner.presentation.ui.theme.SeaFoam
import com.hellmod.sailplanner.presentation.ui.theme.SunsetOrange
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    onTripClick: (String) -> Unit,
    onCreateTrip: () -> Unit,
    viewModel: TripListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Trips", style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTrip,
                containerColor = SunsetOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create trip")
            }
        }
    ) { innerPadding ->
        if (state.activeTrip != null) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                ActiveTripBanner(
                    trip = state.activeTrip!!,
                    onClick = { onTripClick(state.activeTrip!!.id) }
                )
                TripList(
                    trips = state.trips.filter { it.status != TripStatus.ACTIVE },
                    onTripClick = onTripClick
                )
            }
        } else {
            TripList(
                trips = state.trips,
                onTripClick = onTripClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun ActiveTripBanner(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SeaFoam),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF00CC44), CircleShape)
                )
                Text(
                    text = "  ACTIVE NOW",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF00AA33)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(trip.name, style = MaterialTheme.typography.titleLarge)
            Text(
                "${trip.startPort.name} → ${trip.endPort?.name ?: "En route"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TripList(
    trips: List<Trip>,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trips, key = { it.id }) { trip ->
            TripCard(trip = trip, onClick = { onTripClick(trip.id) })
        }
    }
}

@Composable
private fun TripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(trip.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${trip.startPort.name} → ${trip.endPort?.name ?: "TBD"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))
                TripStatusChip(trip.status)
            }
        }
    }
}

@Composable
private fun TripStatusChip(status: TripStatus) {
    val (label, color) = when (status) {
        TripStatus.PLANNED -> "Planned" to OceanBlue
        TripStatus.ACTIVE -> "Active" to Color(0xFF00AA33)
        TripStatus.COMPLETED -> "Completed" to Color(0xFF888888)
        TripStatus.ARCHIVED -> "Archived" to Color(0xFFAAAAAA)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = color)
    }
}
