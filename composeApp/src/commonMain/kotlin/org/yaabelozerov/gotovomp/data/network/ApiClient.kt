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
    suspend fun singUp(body: SignUpRequest): Result<HttpResponse> = runCatching {
        httpClient.post {
            url("Auth/SignUp")
            setBody(body)
        }.body()
    }

    suspend fun singIn(body: SignInWithUsernameRequest): Result<HttpResponse> = runCatching {
       httpClient.post {
           url("Auth/SignIn")
           setBody(body)
       }.body()
    } // TODO реализовать ингридиенты

    suspend fun singIn(body: SignInWithPhoneNumberRequest): Result<SignInDTO> = runCatching {
        httpClient.post {
            url("Auth/SignIn")
            setBody(body)
        }.body()
    }

    suspend fun getUserData(): Result<UserDTO> = runCatching {
        httpClient.get {
            url("Auth/Get")
        }.body()
    }

    suspend fun getOwnedRecipes(): Result<List<RecipeDTO>> = runCatching {
        httpClient.get {
            url("recipes/GetUsersRecipes")
        }.body()
    }

    suspend fun getRecipeById(id: Long): Result<RecipeDTO> = runCatching {
        httpClient.get {
            url("recipes/Get/$id")
        }.body()
    }

    suspend fun getRecipeFeed(limit: Int, offset: Int): Result<List<RecipeDTO>> = runCatching {
        httpClient.get {
            url("recipes/GetUserRecipesFeed")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    suspend fun addRecipe(body: RecipeCreateRequest): Result<HttpResponse> = runCatching {
        httpClient.post {
            setBody(body)
            url("recipes/Add")
        }.body()
    }

    suspend fun deleteRecipe(id: Long): Result<HttpResponse> = runCatching {
        httpClient.delete {
            url("/recipes/Delete/$id")
        }
    }
}