package ru.lipt.domain.login

import ru.lipt.core.encrypt.PinCrypt
import ru.lipt.domain.login.models.ILoginInteractor
import ru.lipt.domain.session.SessionRepository

class LoginInteractor(
    private val loginRepository: LoginRepository,
    private val sessionRepository: SessionRepository,
): ILoginInteractor {

    override suspend fun setPin(pin: String) {
        val session = sessionRepository.getSession()
        if (!session.isEnabled) throw IllegalArgumentException()

        val token = loginRepository.generatePinToken()
        val encryptedPin = PinCrypt.encrypt(token, pin)
        sessionRepository.saveData(encryptedPin)
    }
}
