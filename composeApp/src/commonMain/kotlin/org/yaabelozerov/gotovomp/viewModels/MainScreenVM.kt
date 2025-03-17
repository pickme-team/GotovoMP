package org.yaabelozerov.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.gotovomp.domain.DomainError
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.GlobalEvent
import org.yaabelozerov.gotovomp.domain.UI
import org.yaabelozerov.gotovomp.domain.use_cases.FeedUseCase

data class MainScreenVMState(
    val recipes: List<RecipeDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: DomainError? = null,
)

class FeedScreenVM(
    private val client: ApiClient,
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
