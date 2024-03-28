package ru.lipt.domain.login

import ru.lipt.domain.login.models.CreateTokenRemoteResponse

interface LoginDataSource {
    suspend fun setPin(userId: String, pin: String)
    suspend fun generatePinToken(): CreateTokenRemoteResponse
}
