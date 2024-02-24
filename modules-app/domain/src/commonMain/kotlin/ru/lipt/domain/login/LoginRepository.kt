package ru.lipt.domain.login

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.session.models.Session

class LoginRepository(
    localDataSource: LoginLocalDataSource,
    private val remoteDataSource: LoginDataSource
) : CachePolicyRepository<String, Unit>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
) {
    suspend fun register(email: String, password: String): Session = remoteDataSource.register(
        AuthRequestModel(email, password)
    )

    suspend fun setPin(userId: String, pin: String) {
        remoteDataSource.setPin(userId, pin)
    }

    suspend fun enterAuthData(email: String, password: String): Session = remoteDataSource.enterAuthData(
        AuthRequestModel(email, password)
    )

    suspend fun login(userId: String, encryptedPin: String): Session = remoteDataSource.login(userId, encryptedPin)

    suspend fun revokeDeviceTokens(userId: String) = remoteDataSource.revokeDeviceTokens(userId)

    suspend fun generatePinToken(): String = remoteDataSource.generatePinToken()
}
