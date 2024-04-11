package ru.lipt.domain.login

interface ILoginRepository {
    suspend fun generatePinToken(): String
}
