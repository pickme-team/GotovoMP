package org.yaabelozerov.gotovomp.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.RecipePagingSource
import org.yaabelozerov.gotovomp.data.network.createPager
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO

class FeedUseCase(
    private val api: ApiClient
) {
    fun getFeedPager(): Pager<Int, RecipeDTO> = createPager(api)

    fun likeRecipe() {
        TODO("Not yet implemented")
    }

    fun commentRecipe(text: String) {
        TODO("Not yet implemented")
    }
}