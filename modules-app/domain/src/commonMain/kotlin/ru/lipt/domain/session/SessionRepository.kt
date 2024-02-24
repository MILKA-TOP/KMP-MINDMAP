package ru.lipt.domain.session

import ru.lipt.domain.session.models.Session

class SessionRepository(
    private val dataSource: SessionDataSource,
) {

    var session: Session = Session()

    suspend fun start(session: Session) {
        this.session = session
    }

    suspend fun saveData(pinKey: String) {
        if (!session.isEnabled) throw IllegalArgumentException()

        dataSource.saveSession(session, pinKey)
    }

    suspend fun reset() {
        session = Session()
    }

    suspend fun containsSavedData(): Boolean = dataSource.isContainsAuthData()

    suspend fun getSavedPinKey(): String = dataSource.getPinKey()
    suspend fun getSavedUserId(): String = dataSource.getUserId()

    suspend fun logOut() {
        this.session = Session()
        dataSource.clearSession()
    }
}
