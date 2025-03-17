package org.yaabelozerov.gotovomp.domain.use_cases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.RecipePagingSource
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO

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