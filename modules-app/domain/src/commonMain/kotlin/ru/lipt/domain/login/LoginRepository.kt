package ru.lipt.domain.login

class LoginRepository(
    private val remoteDataSource: LoginDataSource
) : ILoginRepository {

    override suspend fun generatePinToken(): String = remoteDataSource.generatePinToken().token
}
