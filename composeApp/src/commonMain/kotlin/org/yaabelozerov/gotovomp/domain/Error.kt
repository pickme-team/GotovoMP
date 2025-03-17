package org.yaabelozerov.gotovomp.domain

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

fun <T> Result<T>.onError(block: (DomainError) -> Unit): Result<T> {
    if (isFailure) {
        block(exceptionOrNull()?.toError() ?: DomainError.Unknown)
    }
    return this
}

fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    getOrNull()?.let {
        block(it)
    }
    return this
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
