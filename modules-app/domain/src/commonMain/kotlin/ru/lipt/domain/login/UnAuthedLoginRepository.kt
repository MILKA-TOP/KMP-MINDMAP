package ru.lipt.domain.login

import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.login.models.LoginRequestRemote
import ru.lipt.domain.session.models.Session

class UnAuthedLoginRepository(
    private val remoteDataSource: UnAuthedLoginDataSource
) : IUnAuthedLoginRepository {

    override suspend fun register(email: String, password: String): Session = remoteDataSource.register(
        AuthRequestModel(email, password)
    )

    override suspend fun enterAuthData(email: String, password: String): Session = remoteDataSource.enterAuthData(
        AuthRequestModel(email, password)
    )

    override suspend fun login(userId: String, encryptedPin: String): Session =
        remoteDataSource.login(LoginRequestRemote(userId, encryptedPin))

    override suspend fun revokeDeviceTokens(userId: String): Unit = remoteDataSource.revokeDeviceTokens(userId)
}
