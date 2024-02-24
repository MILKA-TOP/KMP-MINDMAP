package ru.lipt.domain.login

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.login.models.RegistryRequestModel
import ru.lipt.domain.session.models.Session

interface LoginDataSource : RemoteDataSource<String, Unit> {
    suspend fun register(request: RegistryRequestModel): Session
    suspend fun setPin(userId: String, pin: String)
    suspend fun generatePinToken(): String
    suspend fun login(userId: String, encryptedPin: String): Session
    suspend fun revokeDeviceTokens(userId: String)
    suspend fun enterAuthData(email: String, password: String): Session
}
