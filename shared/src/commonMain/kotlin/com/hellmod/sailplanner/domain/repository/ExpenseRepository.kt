package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.Balance
import com.hellmod.sailplanner.domain.model.Expense
import com.hellmod.sailplanner.domain.model.Settlement
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(tripId: String): Flow<List<Expense>>
    fun observeExpense(expenseId: String): Flow<Expense?>
    suspend fun addExpense(expense: Expense): Result<Expense>
    suspend fun updateExpense(expense: Expense): Result<Expense>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
    suspend fun markSplitPaid(expenseId: String, userId: String): Result<Unit>
    suspend fun getBalances(tripId: String): Result<List<Balance>>
    suspend fun getSettlements(tripId: String): Result<List<Settlement>>
}
