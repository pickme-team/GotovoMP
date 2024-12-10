package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun CreateHttpClient(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(Logging) {
            level = LogLevel.INFO
        }
        install(ContentNegotiation){
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

}