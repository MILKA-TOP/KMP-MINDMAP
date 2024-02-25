package ru.lipt.domain.login

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.login.models.CreateTokenRemoteResponse
import ru.lipt.domain.login.models.LoginRequestRemote
import ru.lipt.domain.session.models.Session

interface LoginDataSource : RemoteDataSource<String, Unit> {
    suspend fun register(request: AuthRequestModel): Session
    suspend fun setPin(userId: String, pin: String)
    suspend fun generatePinToken(): CreateTokenRemoteResponse
    suspend fun login(request: LoginRequestRemote): Session
    suspend fun revokeDeviceTokens(userId: String)
    suspend fun enterAuthData(request: AuthRequestModel): Session
}
