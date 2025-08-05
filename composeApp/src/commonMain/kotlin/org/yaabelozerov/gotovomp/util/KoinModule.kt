package org.yaabelozerov.gotovomp.util

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.yaabelozerov.gotovomp.data.RecipeRepoImpl
import org.yaabelozerov.gotovomp.data.local.Database.Dao
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.domain.RecipeRepo
import org.yaabelozerov.gotovomp.viewModels.AuthVM
import org.yaabelozerov.gotovomp.viewModels.FeedScreenVM
import org.yaabelozerov.gotovomp.viewModels.PersonalVM
import org.yaabelozerov.gotovomp.viewModels.ProfileVM
import org.yaabelozerov.gotovomp.viewModels.ViewRecipeVM

private object NapierLogger : Logger {
    override fun log(message: String) {
        Napier.d { message }
    }
}

object KoinModule {
    private const val BASE_URL = "http://45.43.77.227:8080/"
    val network = module {
        single {
            HttpClient {
                install(Logging) {
                    logger = NapierLogger
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
        viewModel { PersonalVM(get(), get()) }
        viewModel { ProfileVM(get()) }
        viewModel { ViewRecipeVM(get()) }
    }
    val domain = module {
        single { Dao(createDriver()) }
        single<RecipeRepo> { RecipeRepoImpl(get(), get(), get()) }
    }
}