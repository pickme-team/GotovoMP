package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.DomainError
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.data.network.RecipePagingSource
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.domain.use_cases.FeedUseCase

data class MainScreenVMState(
    val recipes: List<RecipeDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: DomainError? = null,
)

class FeedScreenVM(
    private val client: ApiClient = ApiClient(Net.client),
    private val useCase: FeedUseCase = FeedUseCase(client)
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenVMState())
    val state = _state.asStateFlow()

    val recipes = useCase.getFeedFlow()

    init {
        subscribeToGlobalEvents()
    }

    private fun subscribeToGlobalEvents() {
        viewModelScope.launch {
            UI.GlobalEventFlow.collect {
                when (it) {
                    GlobalEvent.Login -> Unit
                    GlobalEvent.Logout -> _state.update { MainScreenVMState() }
                }
            }
        }
    }

    fun loadRecipes() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.update { it.copy(isLoading = true) }
//            client.getRecipeFeed() unwrap { recipes ->
//                _state.update { it.copy(recipes = recipes, isLoading = false) }
//            } otherwise { err ->
//                _state.update { it.copy(error = err, isLoading = false) }
//            }
//        }
    }
}
