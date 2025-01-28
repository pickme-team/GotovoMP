package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.data.local.settings.SettingsManager
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.data.network.model.RecipeCreateRequest
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.DomainError
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.nullIfBlank

data class CreatedByMeState(
    val recipes: List<RecipeDTO> = emptyList(),
    val error: DomainError? = null,
)

data class PersonalState(
    val recipes: CreatedByMeState = CreatedByMeState(),
    val isLoading: Boolean = false
)

class PersonalVM(private val api: ApiClient = ApiClient(Net.client)): ViewModel() {
    private val _state = MutableStateFlow(PersonalState())
    val state = _state.asStateFlow()

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
    }

    private suspend fun createRecipe(recipe: RecipeCreateRequest) {
        SettingsManager.token.nullIfBlank()?.let {
            api.addRecipe(recipe)
        }
    }
}