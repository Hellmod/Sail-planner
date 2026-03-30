package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.ShoppingItem
import com.hellmod.sailplanner.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun observeShoppingLists(tripId: String): Flow<List<ShoppingList>>
    fun observeShoppingList(listId: String): Flow<ShoppingList?>
    suspend fun createShoppingList(list: ShoppingList): Result<ShoppingList>
    suspend fun updateShoppingList(list: ShoppingList): Result<ShoppingList>
    suspend fun deleteShoppingList(listId: String): Result<Unit>
    suspend fun addItem(item: ShoppingItem): Result<ShoppingItem>
    suspend fun updateItem(item: ShoppingItem): Result<ShoppingItem>
    suspend fun deleteItem(itemId: String): Result<Unit>
    suspend fun markItemPurchased(itemId: String, purchasedBy: String): Result<Unit>
    suspend fun markItemUnpurchased(itemId: String): Result<Unit>
}
