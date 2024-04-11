package ru.lipt.domain.login

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.login.models.CreateTokenRemoteResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
class LoginRepositoryTest : TestsWithMocks() {

    @Mock
    lateinit var remoteDataSource: LoginDataSource

    private val loginRepository by withMocks { LoginRepository(remoteDataSource) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `generatePinToken calls remote data source and returns token`() = runTest {
        val expectedToken = "token123"
        everySuspending { remoteDataSource.generatePinToken() } returns CreateTokenRemoteResponse(expectedToken)

        val result = loginRepository.generatePinToken()

        verifyWithSuspend(exhaustive = false) { remoteDataSource.generatePinToken() }
        assertEquals(expectedToken, result)
    }

    @Test
    fun `generatePinToken propagates exceptions thrown by remote data source`() = runTest {
        val exceptionMessage = "Network error"
        everySuspending { remoteDataSource.generatePinToken() } runs { throw Exception(exceptionMessage) }

        assertFailsWith<Exception>(exceptionMessage) {
            loginRepository.generatePinToken()
        }
    }
}
