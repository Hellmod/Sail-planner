package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

data class ShoppingList(
    val id: String,
    val tripId: String,
    val name: String,
    val category: ShoppingCategory,
    val items: List<ShoppingItem>,
    val createdBy: String,
    val createdAt: Instant
)

data class ShoppingItem(
    val id: String,
    val listId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val estimatedPrice: Double?,
    val actualPrice: Double?,
    val currency: String,
    val isPurchased: Boolean,
    val purchasedBy: String?,
    val purchasedAt: Instant?,
    val notes: String?
)

enum class ShoppingCategory {
    FOOD,
    BEVERAGES,
    SAFETY_EQUIPMENT,
    NAVIGATION,
    MAINTENANCE,
    CLOTHING,
    MEDICAL,
    ENTERTAINMENT,
    OTHER
}
