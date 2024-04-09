package ru.lipt.domain.login

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.session.ISessionRepository
import ru.lipt.domain.session.models.Session
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
class LoginInteractorTest : TestsWithMocks() {

    @Mock
    lateinit var loginRepository: ILoginRepository

    @Mock
    lateinit var sessionRepository: ISessionRepository

    private val loginInteractor by withMocks { LoginInteractor(loginRepository, sessionRepository) }
    override fun setUpMocks() = injectMocks(mocker)

    private val someSalt = "6681d09b000696009b60d193bf2311b48c5e91802de547c3cabb73f79ffa1c2d"

    private val enabledSession: Session = Session(
        sessionId = "some-session-id",
        userId = "1111",
        userEmail = "user@mail.com"
    )

    // Test 1: Successfully setting a PIN when session is enabled
    @Test
    fun `setPin successfully sets encrypted pin when session is enabled`() = runTest {
        val session = Session(userId = "1", userEmail = "u", sessionId = "1")
        val pin = "1234"
        val token = someSalt

        everySuspending { sessionRepository.getSession() } returns session
        everySuspending { loginRepository.generatePinToken() } returns token
        everySuspending { sessionRepository.saveData(isAny()) } returns Unit

        loginInteractor.setPin(pin)

        verifyWithSuspend(exhaustive = false) {
            sessionRepository.getSession()
            loginRepository.generatePinToken()
            sessionRepository.saveData(isAny())
        }
    }

    // Test 2: Verifying IllegalArgumentException is thrown for disabled sessions
    @Test
    fun `setPin throws IllegalArgumentException for disabled session`() = runTest {
        everySuspending { sessionRepository.getSession() } returns Session()

        assertFailsWith<IllegalArgumentException> {
            loginInteractor.setPin("1234")
        }
    }

    // Test 3: Verifying behavior when PIN token generation fails
    @Test
    fun `setPin handles exceptions from generatePinToken`() = runTest {
        everySuspending { sessionRepository.getSession() } returns enabledSession
        everySuspending { loginRepository.generatePinToken() } runs { throw Exception("Token generation failed") }

        assertFailsWith<Exception> {
            loginInteractor.setPin("1234")
        }
    }

    // Test 5: Verifying behavior when saving data fails
    @Test
    fun `setPin handles exceptions from saveData`() = runTest {
        val pin = "1234"
        val token = someSalt
        val exceptionMessage = "Save data failed"
        everySuspending { sessionRepository.getSession() } returns enabledSession
        everySuspending { loginRepository.generatePinToken() } returns token
        everySuspending { sessionRepository.saveData(isAny()) } runs { throw RuntimeException(exceptionMessage) }

        assertFailsWith<RuntimeException>(exceptionMessage) {
            loginInteractor.setPin(pin)
        }
    }
}
