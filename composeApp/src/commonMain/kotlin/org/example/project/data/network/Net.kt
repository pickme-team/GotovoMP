package org.example.project.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.local.settings.SettingsManager
import org.example.project.nullIfBlank

object Net {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val client = HttpClient {
        install(Logging) {
            level = LogLevel.BODY
        }
        install(ContentNegotiation){
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url(BASE_URL)
            headers {
                SettingsManager.token.nullIfBlank()?.let {
                    header("Authorization", "Bearer $it")
                }
            }
        }
        expectSuccess = true
    }
}