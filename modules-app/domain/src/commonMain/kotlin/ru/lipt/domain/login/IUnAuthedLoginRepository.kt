package ru.lipt.domain.login

import ru.lipt.domain.session.models.Session

interface IUnAuthedLoginRepository {
    suspend fun register(email: String, password: String): Session
    suspend fun enterAuthData(email: String, password: String): Session
    suspend fun login(userId: String, encryptedPin: String): Session

    suspend fun revokeDeviceTokens(userId: String): Unit
}
