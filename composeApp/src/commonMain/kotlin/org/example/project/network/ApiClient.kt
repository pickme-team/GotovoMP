package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import org.example.project.data.CatDTO
import org.example.project.network.util.NetworkError
import org.example.project.network.util.Result

class ApiClient(
    private val httpClient: HttpClient
) {
    suspend fun ping(): Result<String, NetworkError> {
        val response = try {
            httpClient.get(
                urlString = "https://raw.githubusercontent.com/mNwoK/cats/refs/heads/main/Requests.json"
            )
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> {
                val cat = response.body<CatDTO>()
                Result.Success(cat.name)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}