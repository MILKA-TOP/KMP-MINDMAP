package ru.lipt.domain.session

import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import org.koin.core.Koin
import org.koin.core.scope.Scope
import ru.lipt.core.di.USER_SESSION_SCOPE_ID
import ru.lipt.domain.session.models.Session

class SessionRepository(
    private val koin: Koin,
    private val dataSource: SessionDataSource,
) {

    private var _session: Session = DEFAULT_SESSION
    private var _koinScope: Scope? = null

    suspend fun getSession(): Session {
        if (_session == DEFAULT_SESSION) {
            _session = Session(
                userId = getSavedUserId(),
            )
        }
        return _session
    }

    suspend fun start(session: Session) {
        closeScope()
        _koinScope = koin.createScope(USER_SESSION_SCOPE_ID, USER_SESSION_SCOPE_QUALIFIER)
        _session = session
    }

    suspend fun saveData(pinKey: String) {
        if (!_session.isEnabled) throw IllegalArgumentException()

        dataSource.saveSession(_session, pinKey)
    }

    suspend fun reset() {
        closeScope()
        _session = DEFAULT_SESSION
    }

    suspend fun containsSavedData(): Boolean = dataSource.isContainsAuthData()

    suspend fun getSavedPinKey(): String = dataSource.getPinKey()
    suspend fun getSavedUserId(): String = dataSource.getUserId()

    suspend fun logOut() {
//        _koinScope?.close()?.also { _koinScope = null }
        closeScope()
        _session = DEFAULT_SESSION
        dataSource.clearSession()
    }

    private fun closeScope() {
        _koinScope?.close()
        _koinScope = null
        koin.deleteScope(USER_SESSION_SCOPE_ID)
    }

    companion object {
        val DEFAULT_SESSION = Session()
    }
}
