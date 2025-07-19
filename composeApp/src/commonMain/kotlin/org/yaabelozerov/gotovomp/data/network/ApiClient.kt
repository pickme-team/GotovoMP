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
import org.yaabelozerov.gotovomp.data.network.model.SignInWithPhoneNumberRequest
import org.yaabelozerov.gotovomp.data.network.model.SignInWithUsernameRequest
import org.yaabelozerov.gotovomp.data.network.model.SignUpRequest
import org.yaabelozerov.gotovomp.data.network.model.UserDTO

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun signUp(body: SignUpRequest): Result<HttpResponse> = runCatching {
        httpClient.post {
            url("auth/signup")
            setBody(body)
        }.body()
    }

    suspend fun signIn(body: SignInWithUsernameRequest): Result<HttpResponse> = runCatching {
       httpClient.post {
           url("auth/signin")
           setBody(body)
       }.body()
    }

    suspend fun signIn(body: SignInWithPhoneNumberRequest): Result<SignInDTO> = runCatching {
        httpClient.post {
            url("auth/signin")
            setBody(body)
        }.body()
    }

    suspend fun getUserData(): Result<UserDTO> = runCatching {
        httpClient.get {
            url("auth/get") // TODO нас ждет глобальное переимнование хуйни
        }.body()
    }

    suspend fun getOwnedRecipes(): Result<List<RecipeDTO>> = runCatching {
        httpClient.get {
            url("recipes/getUsersRecipes")
        }.body()
    }

    suspend fun getRecipeById(id: Long): Result<RecipeDTO> = runCatching {
        httpClient.get {
            url("recipes/get/$id")
        }.body()
    }

    suspend fun getRecipeFeed(limit: Int, offset: Int): Result<List<RecipeDTO>> = runCatching {
        httpClient.get {
            url("recipes/getUserRecipesFeed")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    suspend fun addRecipe(body: RecipeCreateRequest): Result<HttpResponse> = runCatching {
        httpClient.post {
            setBody(body)
            url("recipes/add")
        }.body()
    }

    suspend fun deleteRecipe(id: Long): Result<HttpResponse> = runCatching {
        httpClient.delete {
            url("/recipes/delete/$id")
        }
    }
}