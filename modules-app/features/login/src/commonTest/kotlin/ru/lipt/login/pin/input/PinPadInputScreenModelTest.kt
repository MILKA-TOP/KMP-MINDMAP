package ru.lipt.login.pin.input

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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

@OptIn(ExperimentalCoroutinesApi::class)
class PinPadInputScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var loginInteractor: IUnAuthedLoginInteractor

    private val model by withMocks { PinPadInputScreenModel(loginInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `initial state is correct`() = runTest {
        with(model.uiState.value.model) {
            assertEquals("", pin)
            assertFalse(isSetButtonInProgress)
            assertFalse(isLogOutButtonInProgress)
            assertFalse(showLogOutAlert)
        }
    }

    @Test
    fun `onPinChanged updates pin correctly`() = runTest {
        val pin = "1234"
        model.onPinChanged(pin)
        assertEquals(pin, model.uiState.value.model.pin)
    }

    @Test
    fun `onSubmitPinButtonClick with valid pin navigates to CatalogScreen`() = runTest {
        val pin = "1234"
        model.onPinChanged(pin)
        everySuspending { loginInteractor.login(pin) } returns Unit

        model.onSubmitPinButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogScreenNavigate)
    }

    @Test
    fun `onSubmitPinButtonClick with invalid pin length does not navigate`() = runTest {
        val pin = "123"
        model.onPinChanged(pin)

        model.onSubmitPinButtonClick()

        assertTrue(model.uiState.value.navigationEvents.isEmpty())
    }

    @Test
    fun `onLogoutButtonClick shows logout alert`() = runTest {
        model.onLogoutButtonClick()
        assertTrue(model.uiState.value.model.showLogOutAlert)
    }

    @Test
    fun `onConfirmLogOutAlertButtonClick navigates to HelloScreen`() = runTest {
        everySuspending { loginInteractor.logout() } returns Unit

        model.onConfirmLogOutAlertButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.HelloScreenNavigate)
    }

    @Test
    fun `onCloseLogOutAlert hides logout alert`() = runTest {
        model.onLogoutButtonClick() // Show the alert first
        model.onCloseLogOutAlert()
        assertFalse(model.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `isSetButtonInProgress resets after submit pin completion`() = runTest {
        val pin = "1234"
        model.onPinChanged(pin)
        everySuspending { loginInteractor.login(pin) } returns Unit
        model.onSubmitPinButtonClick()
        advanceUntilIdle()
        assertFalse(model.uiState.value.model.isSetButtonInProgress)
    }

    @Test
    fun `isLogOutButtonInProgress resets after logout completion`() = runTest {
        everySuspending { loginInteractor.logout() } returns Unit

        model.onConfirmLogOutAlertButtonClick()
        advanceTimeBy(2_000)
        advanceUntilIdle()

        assertFalse(model.uiState.value.model.isLogOutButtonInProgress)
    }
}
