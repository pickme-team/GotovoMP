package org.yaabelozerov.gotovomp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import kotlinx.coroutines.delay
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.Pageable
import org.yaabelozerov.gotovomp.domain.PagerSource

class RecipePagingSource(
    private val httpClient: HttpClient
): PagerSource<RecipeDTO> {
    override suspend fun loadPage(
        page: Int,
        pageSize: Int,
    ): Pageable<RecipeDTO> {
        delay(1000)
        return Pageable(items =
            httpClient.get {
                url("recipe/feed")
                parameter("size", pageSize)
                parameter("page", page)
            }.body(), nextKey = page + 1)
    }
}
