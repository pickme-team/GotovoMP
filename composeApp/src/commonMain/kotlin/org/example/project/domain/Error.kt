package org.example.project.domain

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface DomainError {
    enum class NetworkError : DomainError {
        UNAUTHORIZED, NO_INTERNET, SERVER_ERROR, SERIALIZATION, UNKNOWN;
    }
}

sealed interface DomainResult<out T> {
    data class Success<out T>(val value: T) : DomainResult<T>
    data class Error(val error: DomainError) : DomainResult<Nothing>
}

suspend fun <T> wrap(block: suspend () -> T): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (t: Throwable) {
    DomainResult.Error(t.toError())
}

data class UnwrapChainMiddle<T>(
    val result: DomainResult<T>,
    val onSuccess: (T) -> Unit,
)

infix fun <T> DomainResult<T>.unwrap(block: (T) -> Unit) = UnwrapChainMiddle(this, block)

infix fun <T> UnwrapChainMiddle<T>.otherwise(block: (DomainError) -> Unit) = when (this.result) {
    is DomainResult.Success -> this.onSuccess(this.result.value)
    is DomainResult.Error -> block(this.result.error)
}

private fun Throwable?.toError(): DomainError = when (this) {
    is ServerResponseException -> response.status.asNetworkError()
    is ClientRequestException -> response.status.asNetworkError()
    is SerializationException -> DomainError.NetworkError.SERIALIZATION
    is UnresolvedAddressException, is IOException -> DomainError.NetworkError.NO_INTERNET
    else -> DomainError.NetworkError.UNKNOWN
}

private fun HttpStatusCode.asNetworkError(): DomainError.NetworkError = when (this) {
    HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> DomainError.NetworkError.UNAUTHORIZED
    HttpStatusCode.InternalServerError -> DomainError.NetworkError.SERVER_ERROR
    else -> DomainError.NetworkError.UNKNOWN
}
