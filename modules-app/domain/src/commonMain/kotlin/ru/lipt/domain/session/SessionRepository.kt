package ru.lipt.domain.session

import ru.lipt.domain.session.models.Session

class SessionRepository(
    private val dataSource: SessionDataSource,
) {

    private var _session: Session = DEFAULT_SESSION

    suspend fun getSession(): Session {
        if (_session == DEFAULT_SESSION) {
            _session = Session(
                userId = getSavedUserId(),
            )
        }
        return _session
    }

    suspend fun start(session: Session) {
        _session = session
    }

    suspend fun saveData(pinKey: String) {
        if (!_session.isEnabled) throw IllegalArgumentException()

        dataSource.saveSession(_session, pinKey)
    }

    suspend fun reset() {
        _session = DEFAULT_SESSION
    }

    suspend fun containsSavedData(): Boolean = dataSource.isContainsAuthData()

    suspend fun getSavedPinKey(): String = dataSource.getPinKey()
    suspend fun getSavedUserId(): String = dataSource.getUserId()

    suspend fun logOut() {
        _session = DEFAULT_SESSION
        dataSource.clearSession()
    }

    companion object {
        val DEFAULT_SESSION = Session()
    }
}
