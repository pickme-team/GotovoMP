package org.example.project.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap

class RecipePagingSource(
    private val client: ApiClient = ApiClient(Net.client)
): PagingSource<Int, RecipeDTO>() {
    override fun getRefreshKey(state: PagingState<Int, RecipeDTO>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeDTO> {
        val nextPage = params.key ?: 0
        var res: LoadResult<Int, RecipeDTO>? = null
        client.getRecipeFeed(limit = 5, offset = nextPage).unwrap { resp ->
            res = LoadResult.Page(
                data = resp,
                prevKey = null,
                nextKey = (nextPage + 5).takeIf { resp.isNotEmpty() }
            )
        } otherwise {
            res = LoadResult.Error(Throwable(it.toString()))
        }
        return res!!
    }
}