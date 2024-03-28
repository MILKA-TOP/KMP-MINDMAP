package ru.lipt.domain.login

import ru.lipt.core.encrypt.PinCrypt
import ru.lipt.domain.session.SessionRepository

class UnAuthedLoginInteractor(
    private val unAuthedLoginRepository: UnAuthedLoginRepository,
    private val sessionRepository: SessionRepository,
) {

    suspend fun enterAuthData(email: String, password: String) {
        val session = unAuthedLoginRepository.enterAuthData(email, password)
        sessionRepository.start(session)
    }

    suspend fun register(email: String, password: String) {
        val session = unAuthedLoginRepository.register(email, password)
        sessionRepository.start(session)
    }

    suspend fun login(pin: String) {
        val userId = sessionRepository.getSavedUserId()
        val token = sessionRepository.getSavedPinKey()
        val encryptedPin = PinCrypt.encrypt(token, pin)
        val session = unAuthedLoginRepository.login(userId, encryptedPin)

        sessionRepository.start(session)
    }

    suspend fun containsSavedAuthData(): Boolean = sessionRepository.containsSavedData()

    suspend fun logout() {
        val session = sessionRepository.getSession()
        unAuthedLoginRepository.revokeDeviceTokens(session.userId)
        sessionRepository.logOut()
    }
}
