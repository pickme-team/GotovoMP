package org.yaabelozerov.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.data.network.model.UserDTO
import org.yaabelozerov.gotovomp.domain.DomainError
import org.yaabelozerov.gotovomp.domain.onError
import org.yaabelozerov.gotovomp.domain.onSuccess

class ViewRecipeVM(
    private val client: ApiClient,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            client.getRecipeById(id).onSuccess { recipe ->
                _state.update { it.copy(recipe = recipe, isLoading = false) }
            }.onError { err ->
                _state.update { it.copy(error = err, isLoading = false) }
            }
        }
    }

    companion object {
        data class State(
            val recipe: RecipeDTO = RecipeDTO(
                id = 0, name = "", text = "", ingredients = listOf(), author = UserDTO(
                    username = "", firstName = "", lastName = "", phoneNumber = ""
                ), tags = emptyList()
            ),
            val isLoading: Boolean = true,
            val error: DomainError? = null,
        )
    }
}