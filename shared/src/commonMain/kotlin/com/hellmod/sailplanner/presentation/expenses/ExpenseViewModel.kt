package com.hellmod.sailplanner.presentation.expenses

import com.hellmod.sailplanner.domain.model.Balance
import com.hellmod.sailplanner.domain.model.Expense
import com.hellmod.sailplanner.domain.model.Settlement
import com.hellmod.sailplanner.domain.repository.ExpenseRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch

// ── State ──────────────────────────────────────────────────────────────────
data class ExpenseState(
    val isLoading: Boolean = true,
    val expenses: List<Expense> = emptyList(),
    val balances: List<Balance> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
    val totalExpenses: Double = 0.0,
    val selectedExpense: Expense? = null,
    val showBalances: Boolean = false,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface ExpenseIntent : Intent {
    data class LoadExpenses(val tripId: String) : ExpenseIntent
    data class SelectExpense(val expense: Expense) : ExpenseIntent
    data class AddExpense(val expense: Expense) : ExpenseIntent
    data class UpdateExpense(val expense: Expense) : ExpenseIntent
    data class DeleteExpense(val expenseId: String) : ExpenseIntent
    data class MarkSplitPaid(val expenseId: String, val userId: String) : ExpenseIntent
    data class LoadBalances(val tripId: String) : ExpenseIntent
    data object ToggleBalancesView : ExpenseIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface ExpenseEffect : Effect {
    data class ShowError(val message: String) : ExpenseEffect
    data class ShowSuccess(val message: String) : ExpenseEffect
    data object ShowAddExpenseSheet : ExpenseEffect
    data class ShowExpenseDetail(val expenseId: String) : ExpenseEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : BaseViewModel<ExpenseState, ExpenseIntent, ExpenseEffect>(ExpenseState()) {

    override suspend fun handleIntent(intent: ExpenseIntent) {
        when (intent) {
            is ExpenseIntent.LoadExpenses -> loadExpenses(intent.tripId)
            is ExpenseIntent.SelectExpense -> updateState { copy(selectedExpense = intent.expense) }
            is ExpenseIntent.AddExpense -> addExpense(intent.expense)
            is ExpenseIntent.UpdateExpense -> updateExpense(intent.expense)
            is ExpenseIntent.DeleteExpense -> deleteExpense(intent.expenseId)
            is ExpenseIntent.MarkSplitPaid -> markSplitPaid(intent.expenseId, intent.userId)
            is ExpenseIntent.LoadBalances -> loadBalances(intent.tripId)
            ExpenseIntent.ToggleBalancesView -> updateState { copy(showBalances = !showBalances) }
        }
    }

    private fun loadExpenses(tripId: String) {
        viewModelScope.launch {
            expenseRepository.observeExpenses(tripId).collect { expenses ->
                updateState {
                    copy(
                        isLoading = false,
                        expenses = expenses,
                        totalExpenses = expenses.sumOf { it.amount }
                    )
                }
            }
        }
    }

    private suspend fun addExpense(expense: Expense) {
        expenseRepository.addExpense(expense)
            .onSuccess { emitEffect(ExpenseEffect.ShowSuccess("Expense added")) }
            .onFailure { e -> emitEffect(ExpenseEffect.ShowError(e.message ?: "Failed to add expense")) }
    }

    private suspend fun updateExpense(expense: Expense) {
        expenseRepository.updateExpense(expense)
            .onFailure { e -> emitEffect(ExpenseEffect.ShowError(e.message ?: "Failed to update")) }
    }

    private suspend fun deleteExpense(expenseId: String) {
        expenseRepository.deleteExpense(expenseId)
            .onSuccess { emitEffect(ExpenseEffect.ShowSuccess("Expense deleted")) }
            .onFailure { e -> emitEffect(ExpenseEffect.ShowError(e.message ?: "Failed to delete")) }
    }

    private suspend fun markSplitPaid(expenseId: String, userId: String) {
        expenseRepository.markSplitPaid(expenseId, userId)
            .onFailure { e -> emitEffect(ExpenseEffect.ShowError(e.message ?: "Failed to mark as paid")) }
    }

    private suspend fun loadBalances(tripId: String) {
        expenseRepository.getBalances(tripId)
            .onSuccess { balances ->
                val settlements = expenseRepository.getSettlements(tripId).getOrDefault(emptyList())
                updateState { copy(balances = balances, settlements = settlements) }
            }
            .onFailure { e -> emitEffect(ExpenseEffect.ShowError(e.message ?: "Failed to load balances")) }
    }
}
