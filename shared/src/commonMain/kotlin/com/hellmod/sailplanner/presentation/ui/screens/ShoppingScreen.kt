package com.hellmod.sailplanner.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hellmod.sailplanner.domain.model.ShoppingItem
import com.hellmod.sailplanner.domain.model.ShoppingList
import com.hellmod.sailplanner.presentation.shopping.ShoppingIntent
import com.hellmod.sailplanner.presentation.shopping.ShoppingViewModel
import com.hellmod.sailplanner.presentation.ui.theme.OceanBlue
import com.hellmod.sailplanner.presentation.ui.theme.SunsetOrange
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    tripId: String,
    currentUserId: String,
    onBack: () -> Unit,
    viewModel: ShoppingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(tripId) {
        viewModel.dispatch(ShoppingIntent.LoadLists(tripId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Lists", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.dispatch(ShoppingIntent.AddItem(
                    // placeholder – real implementation opens a dialog
                    ShoppingItem(
                        id = "", listId = "", name = "", quantity = 1.0, unit = "pcs",
                        estimatedPrice = null, actualPrice = null, currency = "PLN",
                        isPurchased = false, purchasedBy = null, purchasedAt = null, notes = null
                    )
                )) },
                containerColor = SunsetOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OceanBlue.copy(alpha = 0.08f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Estimated: ${state.totalEstimatedCost.formatCurrency("PLN")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Actual: ${state.totalActualCost.formatCurrency("PLN")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Tab row for lists
            if (state.lists.isNotEmpty()) {
                ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                    state.lists.forEachIndexed { index, list ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                viewModel.dispatch(ShoppingIntent.SelectList(list.id))
                            },
                            text = { Text(list.name) }
                        )
                    }
                }
            }

            // Items list
            val currentList = state.selectedList ?: state.lists.getOrNull(selectedTabIndex)
            if (currentList != null) {
                ShoppingItemsList(
                    list = currentList,
                    onToggle = { itemId ->
                        viewModel.dispatch(ShoppingIntent.TogglePurchased(itemId, currentUserId))
                    }
                )
            } else {
                EmptyShoppingState()
            }
        }
    }
}

@Composable
private fun ShoppingItemsList(list: ShoppingList, onToggle: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val (purchased, notPurchased) = list.items.partition { it.isPurchased }

        items(notPurchased, key = { it.id }) { item ->
            ShoppingItemRow(item = item, onToggle = { onToggle(item.id) })
            HorizontalDivider()
        }

        if (purchased.isNotEmpty()) {
            item {
                Text(
                    "Purchased (${purchased.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(purchased, key = { it.id }) { item ->
                ShoppingItemRow(item = item, onToggle = { onToggle(item.id) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ShoppingItemRow(item: ShoppingItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.isPurchased, onCheckedChange = { onToggle() })
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface.copy(0.4f)
                        else MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${item.quantity} ${item.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        item.estimatedPrice?.let { price ->
            Text(
                price.formatCurrency(item.currency),
                style = MaterialTheme.typography.bodyMedium,
                color = OceanBlue
            )
        }
    }
}

@Composable
private fun EmptyShoppingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("\uD83D\uDED2", style = MaterialTheme.typography.displayLarge)
            Text("No shopping lists yet", style = MaterialTheme.typography.titleMedium)
            Text("Tap + to create one", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        }
    }
}

private fun Double.formatCurrency(currency: String): String =
    "${String.format("%.2f", this)} $currency"
