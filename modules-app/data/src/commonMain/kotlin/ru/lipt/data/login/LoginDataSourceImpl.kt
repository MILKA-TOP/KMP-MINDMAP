package ru.lipt.data.login

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.login.LoginDataSource
import ru.lipt.domain.login.models.RegistryRequestModel
import ru.lipt.domain.session.models.Session

class LoginDataSourceImpl(
    private val config: ApplicationConfig,
    private val client: HttpClient
) : LoginDataSource {
    override suspend fun register(request: RegistryRequestModel): Session =
        client.post(
            urlString = "${config.baseUrl}/registry",
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun generatePinToken(): String = ""

    override suspend fun setPin(userId: String, pin: String) = Unit

    override suspend fun fetch(request: String) = Unit

    override suspend fun login(userId: String, encryptedPin: String) = TEMP_SESSION

    override suspend fun revokeDeviceTokens(userId: String) = Unit

    override suspend fun enterAuthData(email: String, password: String): Session {
        if (password != CORRECT_PASSWORD) throw IllegalArgumentException()
        return TEMP_SESSION
    }

    private companion object {
        val TEMP_SESSION = Session(
            sessionId = "sessionId",
            userId = "userId",
            userEmail = "tmp@gmail.com"
        )

        const val CORRECT_PASSWORD = "password"
    }
}
