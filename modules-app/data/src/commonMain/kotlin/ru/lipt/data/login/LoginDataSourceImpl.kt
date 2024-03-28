package ru.lipt.data.login

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.login.LoginDataSource
import ru.lipt.domain.login.models.CreateTokenRemoteResponse

class LoginDataSourceImpl(
    private val config: ApplicationConfig,
    private val authedClient: HttpClient,
) : LoginDataSource {

    override suspend fun generatePinToken(): CreateTokenRemoteResponse = authedClient.post(
        urlString = "${config.baseUrl}/user/create-token",
    ).body<CreateTokenRemoteResponse>()

    override suspend fun setPin(userId: String, pin: String) = Unit
}
