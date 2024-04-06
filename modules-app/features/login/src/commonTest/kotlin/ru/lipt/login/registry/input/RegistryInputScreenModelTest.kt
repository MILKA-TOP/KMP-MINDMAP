package ru.lipt.login.registry.input

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.login.IUnAuthedLoginInteractor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegistryInputScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var unAuthedLoginInteractor: IUnAuthedLoginInteractor

    private val registryInputScreenModel by withMocks { RegistryInputScreenModel(unAuthedLoginInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `initial state has disabled registry button and empty fields`() = runTest {
        with(registryInputScreenModel.uiState.value.model) {
            assertTrue(email.isEmpty())
            assertTrue(password.isEmpty())
            assertTrue(passwordRepeat.isEmpty())
            assertFalse(registryButtonEnable)
        }
    }

    @Test
    fun `onEmailTextChanged with valid email updates email state`() = runTest {
        val validEmail = "test@example.com"
        registryInputScreenModel.onEmailTextChanged(validEmail)
        assertEquals(validEmail, registryInputScreenModel.uiState.value.model.email)
    }

    @Test
    fun `onPasswordTextChanged updates password state`() = runTest {
        val password = "password123"
        registryInputScreenModel.onPasswordTextChanged(password)
        assertEquals(password, registryInputScreenModel.uiState.value.model.password)
    }

    @Test
    fun `onPasswordRepeatTextChanged updates passwordRepeat state`() = runTest {
        val passwordRepeat = "password123"
        registryInputScreenModel.onPasswordRepeatTextChanged(passwordRepeat)
        assertEquals(passwordRepeat, registryInputScreenModel.uiState.value.model.passwordRepeat)
    }

    @Test
    fun `registry button is enabled when all fields are valid`() = runTest {
        prepareValidInputs()
        assertTrue(registryInputScreenModel.uiState.value.model.registryButtonEnable)
    }

    @Test
    fun `registry button remains disabled with invalid email`() = runTest {
        registryInputScreenModel.onEmailTextChanged("test")
        registryInputScreenModel.onPasswordTextChanged("password123")
        registryInputScreenModel.onPasswordRepeatTextChanged("password123")
        assertFalse(registryInputScreenModel.uiState.value.model.registryButtonEnable)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `show error alert on registry failure`() = runTest {
        everySuspending { unAuthedLoginInteractor.register(isAny(), isAny()) } runs { throw Exception() }
        prepareValidInputs()
        registryInputScreenModel.onRegistryButtonClick()
        advanceTimeBy(3_000) // Allow for coroutine to complete
        assertFalse(registryInputScreenModel.uiState.value.alertErrors.isEmpty())
    }

    @Test
    fun `onRegistryButtonClick with valid input triggers navigation`() = runTest {
        everySuspending { unAuthedLoginInteractor.register(isAny(), isAny()) } returns Unit
        prepareValidInputs()
        registryInputScreenModel.onRegistryButtonClick()
        delay(2000) // Assuming there's a delay in the coroutine execution
        assertTrue(registryInputScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.PinCreateNavigate)
    }

    @Test
    fun `registry button disabled when passwords do not match`() = runTest {
        registryInputScreenModel.onEmailTextChanged("user@example.com")
        registryInputScreenModel.onPasswordTextChanged("password123")
        registryInputScreenModel.onPasswordRepeatTextChanged("password321")
        assertFalse(registryInputScreenModel.uiState.value.model.registryButtonEnable)
    }

    private fun prepareValidInputs() {
        registryInputScreenModel.onEmailTextChanged("test@example.com")
        registryInputScreenModel.onPasswordTextChanged("password123")
        registryInputScreenModel.onPasswordRepeatTextChanged("password123")
    }
}
