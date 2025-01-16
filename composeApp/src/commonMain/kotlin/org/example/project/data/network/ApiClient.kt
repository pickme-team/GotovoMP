package org.example.project.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import org.example.project.data.network.model.SignInWithPhoneNumberRequest
import org.example.project.data.network.model.SignInWithUsernameRequest
import org.example.project.data.network.model.SignUpRequest
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