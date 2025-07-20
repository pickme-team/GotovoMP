package org.yaabelozerov.gotovomp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import org.yaabelozerov.gotovomp.data.network.model.RecipeCreateRequest
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.data.network.model.SignInDTO
import org.yaabelozerov.gotovomp.data.network.model.SignInRequest
import org.yaabelozerov.gotovomp.data.network.model.SignUpRequest
import org.yaabelozerov.gotovomp.data.network.model.UserDTO
import org.yaabelozerov.gotovomp.domain.DomainResult
import org.yaabelozerov.gotovomp.domain.runAndCatch

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun singUp(body: SignUpRequest): DomainResult<HttpResponse> = runAndCatch {
        httpClient.post {
            url("auth/signup")
            setBody(body)
        }.body()
    }

    suspend fun singIn(body: SignInRequest): DomainResult<SignInDTO> = runAndCatch {
        httpClient.post {
            url("auth/signin")
            setBody(body)
        }.body()
    }

    suspend fun getUserData(): DomainResult<UserDTO> = runAndCatch {
        httpClient.get {
            url("auth/get")
        }.body()
    }

    suspend fun getOwnedRecipes(): DomainResult<List<RecipeDTO>> = runAndCatch {
        httpClient.get {
            url("recipes/getUsersRecipes")
        }.body()
    }

    suspend fun getRecipeById(id: Long): DomainResult<RecipeDTO> = runAndCatch {
        httpClient.get {
            url("recipes/get/$id")
        }.body()
    }

    suspend fun getRecipeFeed(limit: Int, offset: Int): DomainResult<List<RecipeDTO>> = runAndCatch {
        httpClient.get {
            url("recipes/getUserRecipesFeed")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    suspend fun addRecipe(body: RecipeCreateRequest): DomainResult<HttpResponse> = runAndCatch {
        httpClient.post {
            setBody(body)
            url("recipes/add")
        }.body()
    }

    suspend fun deleteRecipe(id: Long): DomainResult<HttpResponse> = runAndCatch {
        httpClient.delete {
            url("/recipes/delete/$id")
        }
    }
}