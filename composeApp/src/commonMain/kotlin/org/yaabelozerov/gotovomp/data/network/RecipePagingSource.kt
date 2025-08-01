package org.yaabelozerov.gotovomp.data.network

import androidx.paging.Pager
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.paging.PagingConfig
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.onError
import org.yaabelozerov.gotovomp.domain.onSuccess

class RecipePagingSource(
    private val client: ApiClient
): PagingSource<Int, RecipeDTO>() {
    override fun getRefreshKey(state: PagingState<Int, RecipeDTO>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeDTO> {
        val nextPage = params.key ?: 1
        var res: LoadResult<Int, RecipeDTO>? = null
        client.getRecipeFeed(size = PAGE_SIZE, page = nextPage - 1).onSuccess { resp ->
            res = LoadResult.Page(
                data = resp,
                prevKey = null,
                nextKey = (nextPage - 1 + PAGE_SIZE).takeIf { resp.isNotEmpty() }
            )
        }.onError {
            res = LoadResult.Error(Throwable(it.toString()))
        }
        return res!!
    }

    companion object {
        const val PAGE_SIZE = 2
    }
}

fun createPager(apiClient: ApiClient) = Pager(PagingConfig(RecipePagingSource.PAGE_SIZE)) { RecipePagingSource(apiClient) }