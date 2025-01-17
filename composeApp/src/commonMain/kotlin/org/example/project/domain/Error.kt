package org.example.project.domain

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface DomainError {
    enum class NetworkClientError : DomainError {
        NO_INTERNET, SERIALIZATION;
    }
    enum class NetworkServerError : DomainError {
        UNAUTHORIZED, SERVER_ERROR, CONFLICT;
    }
    data object Unknown : DomainError
}

sealed interface DomainResult<out T> {
    data class Success<out T>(val value: T) : DomainResult<T>
    data class Error(val error: DomainError) : DomainResult<Nothing>
}

suspend fun <T> wrap(logThrowable: Boolean = true, block: suspend () -> T): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (t: Throwable) {
    if (logThrowable) t.printStackTrace()
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
    is SerializationException -> DomainError.NetworkClientError.SERIALIZATION
    is UnresolvedAddressException, is IOException -> DomainError.NetworkClientError.NO_INTERNET
    else -> DomainError.Unknown
}

private fun HttpStatusCode.asNetworkError(): DomainError = when (this) {
    HttpStatusCode.Conflict -> DomainError.NetworkServerError.CONFLICT
    HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> DomainError.NetworkServerError.UNAUTHORIZED
    HttpStatusCode.InternalServerError -> DomainError.NetworkServerError.SERVER_ERROR
    else -> DomainError.Unknown
}
