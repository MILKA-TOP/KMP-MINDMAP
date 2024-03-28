package ru.lipt.domain.login

import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.login.models.LoginRequestRemote
import ru.lipt.domain.session.models.Session

interface UnAuthedLoginDataSource {
    suspend fun register(request: AuthRequestModel): Session
    suspend fun login(request: LoginRequestRemote): Session
    suspend fun revokeDeviceTokens(userId: String)
    suspend fun enterAuthData(request: AuthRequestModel): Session
}
