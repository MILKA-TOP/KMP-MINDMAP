package ru.lipt.core.compose.error

sealed class HttpException(
    val httpCode: Int,
    message: String? = null,
    cause: Throwable?
) : Exception(message, cause) {

    /** 401 */
    class Unauthorized(
        message: String? = null,
        cause: Throwable? = null
    ) : HttpException(401, message, cause)

    /** 4xx */
    class Client(
        code: Int = 400,
        message: String? = null,
        cause: Throwable? = null
    ) : HttpException(code, message, cause)

    /** 5xx */
    class Server(
        code: Int = 500,
        message: String? = null,
        cause: Throwable? = null
    ) : HttpException(code, message, cause)
}
