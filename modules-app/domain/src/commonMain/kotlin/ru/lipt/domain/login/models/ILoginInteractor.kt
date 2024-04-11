package ru.lipt.domain.login.models

interface ILoginInteractor {
    suspend fun setPin(pin: String)
}
