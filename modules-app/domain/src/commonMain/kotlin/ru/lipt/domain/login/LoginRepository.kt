package ru.lipt.domain.login

class LoginRepository(
    private val remoteDataSource: LoginDataSource
) {

    suspend fun generatePinToken(): String = remoteDataSource.generatePinToken().token
}
