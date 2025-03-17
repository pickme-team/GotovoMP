package org.yaabelozerov.gotovomp

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
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.viewModels.AuthVM
import org.yaabelozerov.gotovomp.viewModels.FeedScreenVM
import org.yaabelozerov.gotovomp.viewModels.PersonalVM
import org.yaabelozerov.gotovomp.viewModels.ProfileVM
import org.yaabelozerov.gotovomp.viewModels.ViewRecipeVM

object KoinModule {
    private const val BASE_URL = "https://tarakoshka.tech/gotovo/"
    val network = module {
        single {
            HttpClient {
                install(Logging) {
                    level = LogLevel.BODY
                }
                install(ContentNegotiation){
                    json(json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
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
        factory<ApiClient> { ApiClient(get()) }
    }
    val viewModels = module {
        viewModel { AuthVM(get()) }
        viewModel { FeedScreenVM(get()) }
        viewModel { PersonalVM(get()) }
        viewModel { ProfileVM(get()) }
        viewModel { ViewRecipeVM(get()) }
    }
}