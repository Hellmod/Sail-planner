package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

data class Expense(
    val id: String,
    val tripId: String,
    val title: String,
    val amount: Double,
    val currency: String,
    val category: ExpenseCategory,
    val paidBy: String,
    val splitAmong: List<ExpenseSplit>,
    val date: Instant,
    val notes: String?,
    val receiptImageUrl: String?,
    val createdAt: Instant
)

data class ExpenseSplit(
    val userId: String,
    val userName: String,
    val amount: Double,
    val isPaid: Boolean,
    val paidAt: Instant?
)

enum class ExpenseCategory {
    FOOD,
    FUEL,
    MARINA_FEES,
    REPAIRS,
    EQUIPMENT,
    ENTERTAINMENT,
    TRANSPORT,
    ACCOMMODATION,
    OTHER
}

data class Balance(
    val userId: String,
    val userName: String,
    val totalPaid: Double,
    val totalOwed: Double,
    val netBalance: Double,
    val currency: String
)

data class Settlement(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Double,
    val currency: String
)
