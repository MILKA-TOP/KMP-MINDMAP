package ru.lipt.domain.session

import ru.lipt.domain.session.models.Session

interface SessionDataSource {

    suspend fun saveSession(session: Session, pinKey: String)

    suspend fun clearSession()

    suspend fun isContainsAuthData(): Boolean
}
