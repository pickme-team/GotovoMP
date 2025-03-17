package org.yaabelozerov.gotovomp.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.createPager
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO

class FeedUseCase(
    private val api: ApiClient
) {
    fun getFeedFlow(): Flow<PagingData<RecipeDTO>> = createPager(api).flow

    fun likeRecipe() {
        TODO("Not yet implemented")
    }

    fun commentRecipe(text: String) {
        TODO("Not yet implemented")
    }
}