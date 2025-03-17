package org.example.project.domain.use_cases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.example.project.data.local.settings.SettingsManager
import org.example.project.data.network.ApiClient
import org.example.project.data.network.RecipePagingSource
import org.example.project.data.network.model.RecipeDTO
import org.example.project.nullIfBlank

class FeedUseCase(
    api: ApiClient
) {
    fun getFeedFlow(): Flow<PagingData<RecipeDTO>> {
        val pager = Pager(PagingConfig(pageSize = 1)) {
            RecipePagingSource()
        }.flow
        return pager
    }

    fun likeRecipe() {
        TODO("Not yet implemented")
    }

    fun commentRecipe(text: String) {
        TODO("Not yet implemented")
    }

}