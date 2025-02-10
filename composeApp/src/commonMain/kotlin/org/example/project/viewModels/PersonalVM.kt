package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.local.settings.SettingsManager
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.data.network.model.Ingredient
import org.example.project.data.network.model.IngredientCreateRequest
import org.example.project.data.network.model.RecipeCreateRequest
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.DomainError
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.nullIfBlank
import kotlin.coroutines.CoroutineContext

data class CreatedByMeState(
    val recipes: List<RecipeDTO> = emptyList(),
    val error: DomainError? = null,
)

data class PersonalState(
    val recipes: CreatedByMeState = CreatedByMeState(),
    val isLoading: Boolean = false
)

data class IngredientState(
    val current: List<IngredientCreateRequest> = emptyList(),
    val found: List<IngredientCreateRequest> = emptyList(),
    val query: String = "",
)

class PersonalVM(private val api: ApiClient = ApiClient(Net.client)): ViewModel() {
    private val _state = MutableStateFlow(PersonalState())
    val state = _state.asStateFlow()

    private val _ingredientState = MutableStateFlow(IngredientState())
    val ingredientState = _ingredientState.asStateFlow()

    init {
        fetchRecipes()
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
            api.getRecipes() unwrap { res ->
                _state.update { it.copy(recipes = it.recipes.copy(recipes = res)) }
            } otherwise { err ->
                _state.update { it.copy(recipes = it.recipes.copy(error = err)) }
            }
            _state.update { it.copy(isLoading = false) }
            callback()
        }
    } //TODO("А поч это не в корутине? А то оно в логах пишет, что фреймы пропускаются")

    private suspend fun createRecipe(recipe: RecipeCreateRequest) {
        SettingsManager.token.nullIfBlank()?.let {
            api.addRecipe(recipe)
        }
    }

    fun updateIngredientQuery(query: String) {
        _ingredientState.update { it.copy(query = query) }
        if (query.length <= 2) return
        viewModelScope.launch(Dispatchers.IO) {
            // SEARCH STUB
            val found = emptyList<IngredientCreateRequest>()
            _ingredientState.update { it.copy(found = found) }
        }
    }

    fun addIngredient(request: IngredientCreateRequest) {
        _ingredientState.update {
            it.copy(current = it.current + request)
        }
    }

    fun removeIngredientAt(index: Int) {
        _ingredientState.update {
            it.copy(current = it.current.filterIndexed { idx, _ -> idx != index })
        }
    }

    fun cleanIngredients() {
        _ingredientState.update {
            it.copy(current = emptyList())
        }
    }
}