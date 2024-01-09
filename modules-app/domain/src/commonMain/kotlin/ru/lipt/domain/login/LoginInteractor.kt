package ru.lipt.domain.login

import ru.lipt.core.encrypt.PinCrypt
import ru.lipt.domain.session.SessionRepository

class LoginInteractor(
    private val loginRepository: LoginRepository,
    private val sessionRepository: SessionRepository,
) {

    suspend fun enterAuthData(email: String, password: String) {
        val session = loginRepository.enterAuthData(email, password)
        sessionRepository.start(session)
    }

    suspend fun register(email: String, password: String) {
        val session = loginRepository.register(email, password)
        sessionRepository.start(session)
    }

    suspend fun setPin(pin: String) {
        val session = sessionRepository.session
        if (!session.isEnabled) throw IllegalArgumentException()

        val token = loginRepository.generatePinToken()
        val encryptedPin = PinCrypt.encrypt(token, pin)
        loginRepository.login(session.userId, encryptedPin)
        sessionRepository.saveData(encryptedPin)
    }

    suspend fun logout() {
        val session = sessionRepository.session
        loginRepository.revokeDeviceTokens(session.userId)
        sessionRepository.logOut()
    }
}
