package ru.lipt.domain.login

interface IUnAuthedLoginInteractor {

    suspend fun enterAuthData(email: String, password: String)

    suspend fun register(email: String, password: String)

    suspend fun login(pin: String)

    suspend fun containsSavedAuthData(): Boolean

    suspend fun logout()
}
