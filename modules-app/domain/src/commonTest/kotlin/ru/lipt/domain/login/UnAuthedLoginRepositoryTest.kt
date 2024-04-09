package ru.lipt.domain.login

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.login.models.AuthRequestModel
import ru.lipt.domain.login.models.LoginRequestRemote
import ru.lipt.domain.session.models.Session
import ru.lipt.domain.session.models.fakeSession
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TooGenericExceptionThrown")
@UsesFakes(Session::class)
class UnAuthedLoginRepositoryTest : TestsWithMocks() {

    @Mock
    lateinit var remoteDataSource: UnAuthedLoginDataSource

    private val unAuthedLoginRepository by withMocks { UnAuthedLoginRepository(remoteDataSource) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `register calls remote data source with correct parameters`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val expectedSession: Session = fakeSession()

        everySuspending { remoteDataSource.register(AuthRequestModel(email, password)) } returns expectedSession

        val result = unAuthedLoginRepository.register(email, password)

        assertEquals(expectedSession, result)
        verifyWithSuspend(exhaustive = false) { remoteDataSource.register(AuthRequestModel(email, password)) }
    }

    // Test for the enterAuthData function
    @Test
    fun `enterAuthData calls remote data source with correct parameters`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val expectedSession: Session = fakeSession()

        everySuspending { remoteDataSource.enterAuthData(AuthRequestModel(email, password)) } returns expectedSession

        val result = unAuthedLoginRepository.enterAuthData(email, password)

        assertEquals(expectedSession, result)
        verifyWithSuspend(exhaustive = false) { remoteDataSource.enterAuthData(AuthRequestModel(email, password)) }
    }

    // Test for the login function
    @Test
    fun `login calls remote data source with correct parameters`() = runTest {
        val userId = "userId"
        val encryptedPin = "encryptedPin"
        val expectedSession: Session = fakeSession()

        everySuspending { remoteDataSource.login(LoginRequestRemote(userId, encryptedPin)) } returns expectedSession

        val result = unAuthedLoginRepository.login(userId, encryptedPin)

        assertEquals(expectedSession, result)
        verifyWithSuspend(exhaustive = false) { remoteDataSource.login(LoginRequestRemote(userId, encryptedPin)) }
    }

    // Test for the revokeDeviceTokens function
    @Test
    fun `revokeDeviceTokens calls remote data source with correct parameters`() = runTest {
        val userId = "userId"

        everySuspending { remoteDataSource.revokeDeviceTokens(userId) } returns Unit

        unAuthedLoginRepository.revokeDeviceTokens(userId)

        verifyWithSuspend(exhaustive = false) { remoteDataSource.revokeDeviceTokens(userId) }
    }
}
