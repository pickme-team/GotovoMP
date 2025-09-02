package org.gotovo.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.gotovo.gotovomp.data.local.settings.SettingsManager
import org.gotovo.gotovomp.data.network.ApiClient
import org.gotovo.gotovomp.data.network.model.IngredientCreateRequest
import org.gotovo.gotovomp.data.network.model.RecipeCreateRequest
import org.gotovo.gotovomp.data.network.model.RecipeDTO
import org.gotovo.gotovomp.domain.DomainError
import org.gotovo.gotovomp.domain.GlobalEvent
import org.gotovo.gotovomp.domain.RecipeRepo
import org.gotovo.gotovomp.domain.UI
import org.gotovo.gotovomp.domain.onError
import org.gotovo.gotovomp.domain.onSuccess
import org.gotovo.gotovomp.util.nullIfBlank


class PersonalVM(private val api: ApiClient, private val repo: RecipeRepo): ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        subscribeToGlobalEvents()
    }

    private fun subscribeToGlobalEvents() {
        viewModelScope.launch {
            UI.GlobalEventFlow.collect {
                when (it) {
                    GlobalEvent.Logout -> {
                        _state.update { State() }
                    }
                    GlobalEvent.Login -> fetchRecipes()
                }
            }
        }
    }

    fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) { loadRecipes() }
    }

    fun addRecipe(recipe: RecipeCreateRequest, onCreated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            createRecipe(recipe)
            loadRecipes()
            viewModelScope.launch(Dispatchers.Main) { onCreated() }
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            api.deleteRecipe(recipeId)
            loadRecipes()
        }
    }

    private suspend fun loadRecipes(callback: suspend () -> Unit = {}) {
        SettingsManager.token.nullIfBlank()?.let {
            _state.update { it.copy(isLoading = true) }
            api.getOwnedRecipes().onSuccess { res ->
                _state.update { it.copy(recipes = res) }
            }.onError { err ->
                _state.update { it.copy(error = err) }
            }
            _state.update { it.copy(isLoading = false) }
            callback()
        }
    }

    private suspend fun createRecipe(recipe: RecipeCreateRequest) {
        repo.createRecipe(recipe)
    }

    fun updateIngredientQuery(query: String) {
        _state.update { it.copy(ingredientQuery = query) }
        if (query.length <= 2) return
        viewModelScope.launch(Dispatchers.IO) {
            // SEARCH STUB
            val found = emptyList<IngredientCreateRequest>()
            _state.update { it.copy(foundIngredients = found) }
        }
    }

    fun addIngredient(request: IngredientCreateRequest) {
        _state.update {
            it.copy(currentIngredients = it.currentIngredients + request)
        }
    }

    fun removeIngredientAt(index: Int) {
        _state.update {
            it.copy(currentIngredients = it.currentIngredients.filterIndexed { idx, _ -> idx != index })
        }
    }

    fun cleanIngredients() {
        _state.update {
            it.copy(currentIngredients = emptyList())
        }
    }

    companion object {
        data class State(
            val recipes: List<RecipeDTO> = emptyList(),
            val error: DomainError? = null,
            val isLoading: Boolean = false,
            val currentIngredients: List<IngredientCreateRequest> = emptyList(),
            val foundIngredients: List<IngredientCreateRequest> = emptyList(),
            val ingredientQuery: String = "",
        )
    }
}