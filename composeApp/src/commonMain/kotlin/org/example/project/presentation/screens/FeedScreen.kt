package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.domain.DomainError
import org.example.project.viewModels.FeedScreenVM

@Composable
fun FeedScreen(
    feedScreenVM: FeedScreenVM = viewModel<FeedScreenVM>()
) {
    val uiState by feedScreenVM.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item { uiState.error?.let {
            Text(
                when (it) {
                    is DomainError.NetworkClientError -> "Client Erro"
                    is DomainError.NetworkServerError -> "Server Error"
                    DomainError.Unknown -> "Unknown Error"
                }
            )
        } }
        items(uiState.recipes, key = { it.id }) { recipe ->
            Text(recipe.name)
        }
    }
}
