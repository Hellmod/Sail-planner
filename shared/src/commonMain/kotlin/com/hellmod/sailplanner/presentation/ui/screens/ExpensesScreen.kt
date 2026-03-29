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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.hellmod.sailplanner.domain.model.Balance
import com.hellmod.sailplanner.domain.model.Expense
import com.hellmod.sailplanner.domain.model.Settlement
import com.hellmod.sailplanner.presentation.expenses.ExpenseIntent
import com.hellmod.sailplanner.presentation.expenses.ExpenseViewModel
import com.hellmod.sailplanner.presentation.ui.theme.OceanBlue
import com.hellmod.sailplanner.presentation.ui.theme.SunsetOrange
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    tripId: String,
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(tripId) {
        viewModel.dispatch(ExpenseIntent.LoadExpenses(tripId))
        viewModel.dispatch(ExpenseIntent.LoadBalances(tripId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.dispatch(ExpenseIntent.ToggleBalancesView) },
                containerColor = SunsetOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Total summary
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = OceanBlue.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Expenses", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        Text(
                            "${state.totalExpenses} PLN",
                            style = MaterialTheme.typography.titleLarge,
                            color = OceanBlue
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Members", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        Text(
                            "${state.balances.size}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Expenses") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Balances") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    text = { Text("Settle Up") })
            }

            when (selectedTab) {
                0 -> ExpenseList(expenses = state.expenses)
                1 -> BalanceList(balances = state.balances)
                2 -> SettlementList(settlements = state.settlements)
            }
        }
    }
}

@Composable
private fun ExpenseList(expenses: List<Expense>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(expenses, key = { it.id }) { expense ->
            ExpenseRow(expense = expense)
            HorizontalDivider()
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .background(OceanBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(expense.category.name.first().toString(), style = MaterialTheme.typography.titleMedium)
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(expense.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Paid by: ${expense.paidBy} · ${expense.splitAmong.size} members",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
        }
        Text(
            "${expense.amount} ${expense.currency}",
            style = MaterialTheme.typography.titleMedium,
            color = OceanBlue
        )
    }
}

@Composable
private fun BalanceList(balances: List<Balance>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(balances, key = { it.userId }) { balance ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(balance.userName, style = MaterialTheme.typography.bodyLarge)
                val color = when {
                    balance.netBalance > 0 -> Color(0xFF00AA33)
                    balance.netBalance < 0 -> Color(0xFFCC2200)
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    "${if (balance.netBalance >= 0) "+" else ""}${balance.netBalance} ${balance.currency}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = color
                )
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun SettlementList(settlements: List<Settlement>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(settlements) { s ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${s.fromUserName} → ${s.toUserName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "${s.amount} ${s.currency}",
                            style = MaterialTheme.typography.titleMedium,
                            color = OceanBlue
                        )
                    }
                }
            }
        }
    }
}
