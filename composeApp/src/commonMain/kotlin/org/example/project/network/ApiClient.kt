package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.project.data.CatDTO
import org.example.project.domain.DomainResult
import org.example.project.domain.wrap

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun ping(): DomainResult<CatDTO> = wrap {
        httpClient.get(
            urlString = "https://raw.githubusercontent.com/mNwoK/cats/refs/heads/main/Requests.json"
        ).body()
    }
}