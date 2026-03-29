package com.hellmod.sailplanner.presentation.shopping

import com.hellmod.sailplanner.domain.model.ShoppingCategory
import com.hellmod.sailplanner.domain.model.ShoppingItem
import com.hellmod.sailplanner.domain.model.ShoppingList
import com.hellmod.sailplanner.domain.repository.ShoppingRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch

// ── State ──────────────────────────────────────────────────────────────────
data class ShoppingState(
    val isLoading: Boolean = true,
    val lists: List<ShoppingList> = emptyList(),
    val selectedList: ShoppingList? = null,
    val filterCategory: ShoppingCategory? = null,
    val showPurchased: Boolean = true,
    val totalEstimatedCost: Double = 0.0,
    val totalActualCost: Double = 0.0,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface ShoppingIntent : Intent {
    data class LoadLists(val tripId: String) : ShoppingIntent
    data class SelectList(val listId: String) : ShoppingIntent
    data class AddItem(val item: ShoppingItem) : ShoppingIntent
    data class UpdateItem(val item: ShoppingItem) : ShoppingIntent
    data class DeleteItem(val itemId: String) : ShoppingIntent
    data class TogglePurchased(val itemId: String, val purchasedBy: String) : ShoppingIntent
    data class FilterByCategory(val category: ShoppingCategory?) : ShoppingIntent
    data class ToggleShowPurchased(val show: Boolean) : ShoppingIntent
    data class CreateList(val list: ShoppingList) : ShoppingIntent
    data class DeleteList(val listId: String) : ShoppingIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface ShoppingEffect : Effect {
    data class ShowError(val message: String) : ShoppingEffect
    data class ShowSuccess(val message: String) : ShoppingEffect
    data object ShowAddItemDialog : ShoppingEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository
) : BaseViewModel<ShoppingState, ShoppingIntent, ShoppingEffect>(ShoppingState()) {

    override suspend fun handleIntent(intent: ShoppingIntent) {
        when (intent) {
            is ShoppingIntent.LoadLists -> loadLists(intent.tripId)
            is ShoppingIntent.SelectList -> selectList(intent.listId)
            is ShoppingIntent.AddItem -> addItem(intent.item)
            is ShoppingIntent.UpdateItem -> updateItem(intent.item)
            is ShoppingIntent.DeleteItem -> deleteItem(intent.itemId)
            is ShoppingIntent.TogglePurchased -> togglePurchased(intent.itemId, intent.purchasedBy)
            is ShoppingIntent.FilterByCategory -> updateState { copy(filterCategory = intent.category) }
            is ShoppingIntent.ToggleShowPurchased -> updateState { copy(showPurchased = intent.show) }
            is ShoppingIntent.CreateList -> createList(intent.list)
            is ShoppingIntent.DeleteList -> deleteList(intent.listId)
        }
    }

    private fun loadLists(tripId: String) {
        viewModelScope.launch {
            shoppingRepository.observeShoppingLists(tripId).collect { lists ->
                updateState {
                    copy(
                        isLoading = false,
                        lists = lists,
                        totalEstimatedCost = lists.flatMap { it.items }
                            .mapNotNull { it.estimatedPrice }
                            .sum(),
                        totalActualCost = lists.flatMap { it.items }
                            .mapNotNull { it.actualPrice }
                            .sum()
                    )
                }
            }
        }
    }

    private fun selectList(listId: String) {
        viewModelScope.launch {
            shoppingRepository.observeShoppingList(listId).collect { list ->
                updateState { copy(selectedList = list) }
            }
        }
    }

    private suspend fun addItem(item: ShoppingItem) {
        shoppingRepository.addItem(item)
            .onFailure { e -> emitEffect(ShoppingEffect.ShowError(e.message ?: "Failed to add item")) }
    }

    private suspend fun updateItem(item: ShoppingItem) {
        shoppingRepository.updateItem(item)
            .onFailure { e -> emitEffect(ShoppingEffect.ShowError(e.message ?: "Failed to update item")) }
    }

    private suspend fun deleteItem(itemId: String) {
        shoppingRepository.deleteItem(itemId)
            .onFailure { e -> emitEffect(ShoppingEffect.ShowError(e.message ?: "Failed to delete item")) }
    }

    private suspend fun togglePurchased(itemId: String, purchasedBy: String) {
        val item = state.value.selectedList?.items?.firstOrNull { it.id == itemId } ?: return
        if (item.isPurchased) {
            shoppingRepository.markItemUnpurchased(itemId)
        } else {
            shoppingRepository.markItemPurchased(itemId, purchasedBy)
        }
    }

    private suspend fun createList(list: ShoppingList) {
        shoppingRepository.createShoppingList(list)
            .onSuccess { emitEffect(ShoppingEffect.ShowSuccess("List created")) }
            .onFailure { e -> emitEffect(ShoppingEffect.ShowError(e.message ?: "Failed to create list")) }
    }

    private suspend fun deleteList(listId: String) {
        shoppingRepository.deleteShoppingList(listId)
            .onSuccess { emitEffect(ShoppingEffect.ShowSuccess("List deleted")) }
            .onFailure { e -> emitEffect(ShoppingEffect.ShowError(e.message ?: "Failed to delete list")) }
    }
}
