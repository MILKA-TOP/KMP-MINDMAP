package ru.lipt.data.login

import ru.lipt.domain.login.LoginDataSource
import ru.lipt.domain.session.models.Session

class LoginDataSourceImpl : LoginDataSource {
    override suspend fun register(email: String, password: String): Session = Session(
        sessionId = "sessionId",
        userId = "userId",
        userEmail = "tmp@gmail.com"
    )

    override suspend fun generatePinToken(): String = ""

    override suspend fun setPin(userId: String, pin: String) = Unit

    override suspend fun fetch(request: String) = Unit

    override suspend fun login(userId: String, encryptedPin: String) = Unit

    override suspend fun revokeDeviceTokens(userId: String) = Unit
}
