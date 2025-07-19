package org.yaabelozerov.gotovomp.domain

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface DomainError {
    val throwable: Throwable

    sealed interface NetworkClientError : DomainError {
        data class NoInternet(override val throwable: Throwable) : NetworkClientError
        data class Serialization(override val throwable: Throwable) : NetworkClientError
    }

    sealed interface NetworkServerError : DomainError {
        data class Unauthorized(override val throwable: Throwable) : NetworkServerError
        data class ServerError(override val throwable: Throwable) : NetworkServerError
        data class Conflict(override val throwable: Throwable) : NetworkServerError
        data class NotFound(override val throwable: Throwable) : NetworkServerError
    }

    data class Unknown(override val throwable: Throwable) : DomainError
}

data class DomainResult<T>(val data: T?, val error: DomainError?)

fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (data != null) action(data)
    return this
}

fun <T> DomainResult<T>.onError(action: (DomainError) -> Unit): DomainResult<T> {
    error?.throwable?.printStackTrace()
    if (error != null) action(error)
    return this
}

suspend fun <T> runAndCatch(block: suspend () -> T): DomainResult<T> {
    val result = runCatching { block() }
    return result.getOrNull()?.let { DomainResult(it, null) } ?: DomainResult(
        null,
        result.exceptionOrNull().toError()
    )
}

private fun Throwable?.toError(): DomainError = when (this) {
    is ServerResponseException -> response.status.asNetworkError(this)
    is ClientRequestException -> response.status.asNetworkError(this)
    is SerializationException -> DomainError.NetworkClientError.Serialization(this)
    is UnresolvedAddressException, is IOException -> DomainError.NetworkClientError.NoInternet(this)
    else -> DomainError.Unknown(this ?: IllegalStateException("Error with no underlying exception"))
}

private fun HttpStatusCode.asNetworkError(throwable: Throwable): DomainError = when (this) {
    HttpStatusCode.Conflict -> DomainError.NetworkServerError.Conflict(throwable)
    HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> DomainError.NetworkServerError.Unauthorized(
       throwable
    )
    HttpStatusCode.NotFound, HttpStatusCode.NoContent -> DomainError.NetworkServerError.NotFound(throwable)

    HttpStatusCode.InternalServerError -> DomainError.NetworkServerError.ServerError(throwable)
    else -> DomainError.Unknown(throwable)
}
