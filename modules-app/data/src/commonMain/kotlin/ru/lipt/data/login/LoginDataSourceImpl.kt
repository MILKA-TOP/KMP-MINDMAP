package ru.lipt.data.login

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.login.LoginDataSource
import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.login.models.CreateTokenRemoteResponse
import ru.lipt.domain.login.models.LoginRequestRemote
import ru.lipt.domain.session.models.Session

class LoginDataSourceImpl(
    private val config: ApplicationConfig,
    private val unAuthedClient: HttpClient,
    private val authedClient: HttpClient,
) : LoginDataSource {
    override suspend fun register(request: AuthRequestModel): Session =
        unAuthedClient.post(
            urlString = "${config.baseUrl}/user/registry",
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun generatePinToken(): CreateTokenRemoteResponse = authedClient.post(
        urlString = "${config.baseUrl}/user/create-token",
    ).body<CreateTokenRemoteResponse>()

    override suspend fun setPin(userId: String, pin: String) = Unit

    override suspend fun fetch(request: String) = Unit

    override suspend fun login(request: LoginRequestRemote) = unAuthedClient.post(
        urlString = "${config.baseUrl}/user/login"
    ) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body<Session>()

    override suspend fun revokeDeviceTokens(userId: String) = Unit

    override suspend fun enterAuthData(request: AuthRequestModel): Session =
        unAuthedClient.post(
            urlString = "${config.baseUrl}/user/enter-auth-data",
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
