package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.local.settings.SettingsManager
import org.example.project.domain.DomainError
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.nullIfBlank

data class MainScreenVMState(
    val recipes: List<RecipeDTO> = emptyList(),
    val error: DomainError? = null,
)

class FeedScreenVM(
    private val api: ApiClient = ApiClient(Net.client)
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenVMState())
    val state = _state.asStateFlow()

    init {
        subscribeToGlobalEvents()
    }

    private fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            SettingsManager.token.nullIfBlank()?.let {
                api.getRecipes() unwrap { res ->
                    _state.update { it.copy(recipes = res) }
                } otherwise { err ->
                    _state.update { it.copy(error = err) }
                }
            }
        }
    }

    private fun subscribeToGlobalEvents() {
        viewModelScope.launch {
            UI.GlobalEventFlow.collect {
                when (it) {
                    GlobalEvent.Logout -> _state.update { MainScreenVMState() }
                    GlobalEvent.Login -> fetchRecipes()
                }
            }
        }
    }
}
