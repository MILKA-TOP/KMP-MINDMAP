package ru.lipt.data.session

import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import ru.lipt.domain.session.SessionDataSource
import ru.lipt.domain.session.models.Session

class SessionDataSourceImpl : SessionDataSource {
    private val settings: Settings = Settings()

    override suspend fun saveSession(session: Session, pinKey: String) {
        settings.putString(USER_ID, session.userId)
        settings.putString(PIN_KEY, pinKey)
    }

    override suspend fun clearSession() {
        settings.remove(USER_ID)
        settings.remove(PIN_KEY)
    }

    override suspend fun isContainsAuthData(): Boolean = settings.contains(USER_ID) && settings.contains(PIN_KEY)

    private companion object {
        const val USER_ID = "USER_ID"
        const val PIN_KEY = "PIN_KEY"
    }
}
