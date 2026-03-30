package com.hellmod.sailplanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hellmod.sailplanner.presentation.ui.theme.OceanBlue
import com.hellmod.sailplanner.presentation.ui.theme.SeaFoam
import com.hellmod.sailplanner.presentation.ui.theme.SunsetOrange

data class DashboardTile(
    val title: String,
    val emoji: String,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDashboardScreen(
    tripId: String,
    tripName: String,
    onBack: () -> Unit,
    onShopping: () -> Unit,
    onWatches: () -> Unit,
    onRoute: () -> Unit,
    onPhotos: () -> Unit,
    onExpenses: () -> Unit,
    onCollage: () -> Unit
) {
    val tiles = listOf(
        DashboardTile("Shopping\nLists", "\uD83D\uDED2", Color(0xFF4A90D9), onShopping),
        DashboardTile("Watches\n(Wachty)", "\u23F0", OceanBlue, onWatches),
        DashboardTile("Route\nTracking", "\uD83D\uDDFA\uFE0F", Color(0xFF2ECC71), onRoute),
        DashboardTile("Trip\nPhotos", "\uD83D\uDCF7", SunsetOrange, onPhotos),
        DashboardTile("Expenses\n(Splitwise)", "\uD83D\uDCB0", Color(0xFF9B59B6), onExpenses),
        DashboardTile("Memory\nCollage", "\uD83C\uDF0A", SeaFoam, onCollage),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tripName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tiles.size) { index ->
                DashboardTileCard(tile = tiles[index])
            }
        }
    }
}

@Composable
private fun DashboardTileCard(tile: DashboardTile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = tile.onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tile.color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(tile.emoji, style = MaterialTheme.typography.headlineMedium)
            Text(
                tile.title,
                style = MaterialTheme.typography.titleMedium,
                color = tile.color
            )
        }
    }
}
