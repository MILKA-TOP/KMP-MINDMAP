package ru.lipt.data.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.errors.IOException
import ru.lipt.core.compose.error.HttpException
import ru.lipt.core.compose.error.NoInternetException
import ru.lipt.domain.session.SessionRepository

class NetworkRequestExceptionsHandler(
    private val sessionRepository: SessionRepository,
) {

    suspend fun handle(cause: Throwable) = when (cause) {
        is HttpRequestTimeoutException,
        is IOException,
        -> throw NoInternetException
        is ResponseException -> handle(cause.response.status.value, cause.response.bodyAsText(), cause)
        else -> throw cause
    }

    private suspend fun handle(code: Int, errorResponse: String, cause: Throwable? = null) {
        when (code) {
            401 -> {
                throw HttpException.Unauthorized(errorResponse, cause).also {
                    sessionRepository.reset()
                }
            }
            in 400..499 -> {
                throw HttpException.Client(code, errorResponse, cause)
            }
            in 500..599 -> {
                val exception = HttpException.Server(code, errorResponse, cause)
                throw exception
            }
        }
    }
}
