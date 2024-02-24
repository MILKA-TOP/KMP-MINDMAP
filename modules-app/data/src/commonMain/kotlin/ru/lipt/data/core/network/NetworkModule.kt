package ru.lipt.data.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.core.network.ExtendedHttpHeaders

object NetworkModule {

    internal fun provideAuthorizedHttpClient(
        json: Json,
        exceptionsHandler: NetworkRequestExceptionsHandler,
        deviceConfig: ApplicationConfig,
    ) = HttpClient {
        expectSuccess = true
        defaultRequest {
            headers {
                append(ExtendedHttpHeaders.DeviceId, deviceConfig.deviceId)
            }
        }
        install(ContentNegotiation) { json(json) }
        install(HttpCallValidator) {
            handleResponseException(exceptionsHandler::handle)
        }
        install(HttpTimeout)
    }

    internal fun provideJson() = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }
}
