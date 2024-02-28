package ru.lipt.data.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.core.network.ExtendedHttpHeaders
import ru.lipt.domain.map.models.abstract.summaryMapJsonModule
import ru.lipt.domain.session.SessionRepository

object NetworkModule {

    internal fun provideBaseHttpClient(
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

    internal fun provideAuthedHttpClient(
        json: Json,
        exceptionsHandler: NetworkRequestExceptionsHandler,
        deviceConfig: ApplicationConfig,
        sessionRepository: SessionRepository,
    ) = HttpClient {
        expectSuccess = true
        defaultRequest {
            headers {
                append(ExtendedHttpHeaders.DeviceId, deviceConfig.deviceId)
            }
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val session = sessionRepository.getSession()
                    BearerTokens(session.sessionId, "")
                }
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
//        encodeDefaults = true
        serializersModule = summaryMapJsonModule
    }
}
