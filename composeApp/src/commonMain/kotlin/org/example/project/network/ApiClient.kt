package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import org.example.project.data.RecipeDTO
import org.example.project.data.SignInWithPhoneNumberRequest
import org.example.project.data.SignInWithUsernameRequest
import org.example.project.data.SignUpRequest
import org.example.project.domain.DomainResult
import org.example.project.domain.wrap

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun singUp(body: SignUpRequest): DomainResult<HttpResponse> = wrap {
        httpClient.post {
            url("auth/signUp")
            setBody(body)
        }.body()
    }

    suspend fun singIn(body: SignInWithUsernameRequest): DomainResult<HttpResponse> = wrap {
       httpClient.post {
           url("auth/signIn")
           setBody(body)
       }.body()
    }

    suspend fun singIn(body: SignInWithPhoneNumberRequest): DomainResult<HttpResponse> = wrap {
        httpClient.post {
            url("auth/signIn")
            setBody(body)
        }.body()
    }
}