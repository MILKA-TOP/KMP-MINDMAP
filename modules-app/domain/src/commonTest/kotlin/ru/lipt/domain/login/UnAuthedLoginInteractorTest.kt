package ru.lipt.domain.login

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.core.encrypt.PinCrypt
import ru.lipt.domain.session.ISessionRepository
import ru.lipt.domain.session.models.Session
import ru.lipt.domain.session.models.fakeSession
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
@UsesFakes(Session::class)
class UnAuthedLoginInteractorTest : TestsWithMocks() {

    @Mock
    lateinit var unAuthedLoginRepository: IUnAuthedLoginRepository

    @Mock
    lateinit var sessionRepository: ISessionRepository
    private val someSalt = "6681d09b000696009b60d193bf2311b48c5e91802de547c3cabb73f79ffa1c2d"

    private val unAuthedLoginInteractor by withMocks { UnAuthedLoginInteractor(unAuthedLoginRepository, sessionRepository) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `enterAuthData starts session with given credentials`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val session: Session = fakeSession()

        everySuspending { unAuthedLoginRepository.enterAuthData(email, password) } returns session
        everySuspending { sessionRepository.start(isAny()) } returns Unit

        unAuthedLoginInteractor.enterAuthData(email, password)

        verifyWithSuspend(exhaustive = false) { unAuthedLoginRepository.enterAuthData(email, password) }
    }

    @Test
    fun `register starts session with new user credentials`() = runTest {
        val email = "newuser@example.com"
        val password = "newpassword123"
        val session: Session = fakeSession()

        everySuspending { unAuthedLoginRepository.register(email, password) } returns session
        everySuspending { sessionRepository.start(session) } returns Unit

        unAuthedLoginInteractor.register(email, password)

        verifyWithSuspend(exhaustive = false) { unAuthedLoginRepository.register(email, password) }
    }

    @Test
    fun `login uses encrypted pin and starts session`() = runTest {
        val pin = "1234"
        val userId = "userId123"
        val token = someSalt
        val encryptedPin = PinCrypt.encrypt(token, pin)
        val session: Session = fakeSession()

        everySuspending { sessionRepository.getSavedUserId() } returns userId
        everySuspending { sessionRepository.getSavedPinKey() } returns token
        everySuspending { unAuthedLoginRepository.login(userId, encryptedPin) } returns session
        everySuspending { sessionRepository.start(isAny()) } returns Unit

        unAuthedLoginInteractor.login(pin)

        verifyWithSuspend(exhaustive = false) { PinCrypt.encrypt(token, pin) }
    }

    @Test
    fun `containsSavedAuthData delegates to sessionRepository`() = runTest {
        everySuspending { sessionRepository.containsSavedData() } returns true

        assertTrue(unAuthedLoginInteractor.containsSavedAuthData())

        verifyWithSuspend(exhaustive = false) { sessionRepository.containsSavedData() }
    }

    @Test
    fun `logout revokes device tokens and logs out session`() = runTest {
        val userId = "userId123"
        val session: Session = fakeSession().copy(userId = userId)

        everySuspending { sessionRepository.getSession() } returns session
        everySuspending { unAuthedLoginRepository.revokeDeviceTokens(userId) } returns Unit
        everySuspending { sessionRepository.logOut() } returns Unit

        unAuthedLoginInteractor.logout()
        advanceUntilIdle()
        verifyWithSuspend(exhaustive = false) { unAuthedLoginRepository.revokeDeviceTokens(userId) }
    }
}
