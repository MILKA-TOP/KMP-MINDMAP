package ru.lipt.domain.session

import ru.lipt.domain.session.models.Session

interface ISessionRepository {
    suspend fun getSession(): Session

    suspend fun start(session: Session)

    suspend fun saveData(pinKey: String)

    suspend fun reset()

    suspend fun containsSavedData(): Boolean

    suspend fun getSavedPinKey(): String
    suspend fun getSavedUserId(): String

    suspend fun logOut()
}
