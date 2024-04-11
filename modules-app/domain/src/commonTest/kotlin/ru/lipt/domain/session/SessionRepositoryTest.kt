package ru.lipt.domain.session

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Fake
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import org.koin.core.Koin
import ru.lipt.core.di.USER_SESSION_SCOPE_ID
import ru.lipt.domain.session.models.Session
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Suppress("TooGenericExceptionThrown")
@UsesFakes(Session::class, Koin::class)
class SessionRepositoryTest : TestsWithMocks() {

    @Mock
    lateinit var dataSource: SessionDataSource

    @Fake
    lateinit var koin: Koin

    private val enabledSession: Session = Session(
        sessionId = "some-session-id",
        userId = "1111",
        userEmail = "user@mail.com"
    )

    private val sessionRepository by withMocks { SessionRepository(koin, dataSource) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `getSession returns default session initially`() = runTest {
        everySuspending { dataSource.getUserId() } returns ""
        assertEquals(SessionRepository.DEFAULT_SESSION, sessionRepository.getSession())
    }

    @Test
    fun `start creates a new Koin scope and updates session`() = runTest {
        val newSession = Session(userId = "user123")

        sessionRepository.start(newSession)

        assertEquals(newSession, sessionRepository.getSession())
    }

    @Test
    fun `saveData throws IllegalArgumentException when session is disabled`() = runTest {
        val disabledSession = Session()
        sessionRepository.start(disabledSession)

        assertFailsWith<IllegalArgumentException> {
            sessionRepository.saveData("pinKey")
        }
    }

    @Test
    fun `reset closes Koin scope and resets session to default`() = runTest {
        everySuspending { dataSource.getUserId() } returns ""
        sessionRepository.reset()

        verifyWithSuspend { koin.deleteScope(USER_SESSION_SCOPE_ID) }
        assertEquals(SessionRepository.DEFAULT_SESSION, sessionRepository.getSession())
    }

    @Test
    fun `containsSavedData delegates to dataSource`() = runTest {
        everySuspending { dataSource.isContainsAuthData() } returns true

        assertTrue(sessionRepository.containsSavedData())
        verifyWithSuspend(exhaustive = false) { dataSource.isContainsAuthData() }
    }

    @Test
    fun `logOut clears session data and closes Koin scope`() = runTest {
        everySuspending { dataSource.clearSession() } returns Unit
        everySuspending { dataSource.getUserId() } returns ""

        sessionRepository.logOut()

        verifyWithSuspend(exhaustive = false) { dataSource.clearSession() }
        assertEquals(SessionRepository.DEFAULT_SESSION, sessionRepository.getSession())
    }

    @Test
    fun `getSavedPinKey returns pin key from dataSource`() = runTest {
        val expectedPinKey = "pinKey123"
        everySuspending { dataSource.getPinKey() } returns expectedPinKey

        val result = sessionRepository.getSavedPinKey()

        assertEquals(expectedPinKey, result)
        verifyWithSuspend(exhaustive = false) { dataSource.getPinKey() }
    }

    @Test
    fun `saveData saves session data when session is enabled`() = runTest {
        val enabledSession = enabledSession
        val pinKey = "pinKey123"
        sessionRepository.start(enabledSession)

        everySuspending { dataSource.saveSession(isAny(), isAny()) } returns Unit

        sessionRepository.saveData(pinKey)

        verifyWithSuspend(exhaustive = false) { dataSource.saveSession(enabledSession, pinKey) }
    }
}
