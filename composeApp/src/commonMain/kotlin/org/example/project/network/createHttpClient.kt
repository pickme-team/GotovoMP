package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType

import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.serialization
import kotlinx.serialization.json.Json

const val BASE_URL = "http://10.0.2.2:8080/"

fun createHttpClient(): HttpClient {
    return HttpClient {
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
        }
        expectSuccess = true
    }
}