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
import org.yaabelozerov.gotovomp.domain.DomainResult
import org.yaabelozerov.gotovomp.domain.wrap

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun singUp(body: SignUpRequest): DomainResult<HttpResponse> = wrap {
        httpClient.post {
            url("Auth/SignUp")
            setBody(body)
        }.body()
    }

    suspend fun singIn(body: SignInWithUsernameRequest): DomainResult<HttpResponse> = wrap {
       httpClient.post {
           url("Auth/SignIn")
           setBody(body)
       }.body()
    } // TODO реализовать ингридиенты

    suspend fun singIn(body: SignInWithPhoneNumberRequest): DomainResult<SignInDTO> = wrap {
        httpClient.post {
            url("Auth/SignIn")
            setBody(body)
        }.body()
    }

    suspend fun getUserData(): DomainResult<UserDTO> = wrap {
        httpClient.get {
            url("Auth/Get")
        }.body()
    }

    suspend fun getOwnedRecipes(): DomainResult<List<RecipeDTO>> = wrap {
        httpClient.get {
            url("recipes/GetUsersRecipes")
        }.body()
    }

    suspend fun getRecipeById(id: Long): DomainResult<RecipeDTO> = wrap {
        httpClient.get {
            url("recipes/Get/$id")
        }.body()
    }

    suspend fun getRecipeFeed(limit: Int, offset: Int): DomainResult<List<RecipeDTO>> = wrap {
        httpClient.get {
            url("recipes/GetUserRecipesFeed")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    suspend fun addRecipe(body: RecipeCreateRequest): DomainResult<HttpResponse> = wrap {
        httpClient.post {
            setBody(body)
            url("recipes/Add")
        }.body()
    }

    suspend fun deleteRecipe(id: Long): DomainResult<HttpResponse> = wrap {
        httpClient.delete {
            url("/recipes/Delete/$id")
        }
    }
}