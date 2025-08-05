package org.yaabelozerov.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import org.yaabelozerov.gotovomp.data.network.RecipePagingSource
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.Pager

class FeedScreenVM(
    private val httpClient: HttpClient,
) : ViewModel() {
    val pager: Pager<RecipeDTO> by lazy {
        Pager(
            source = RecipePagingSource(
                httpClient = httpClient
            ),
            pageSize = 5,
            scope = viewModelScope,
            initialPage = 0
        )
    }
}