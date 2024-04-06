package ru.lipt.login.pin.create

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.login.models.ILoginInteractor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PinPadCreateScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var loginInteractor: ILoginInteractor

    private val model by withMocks { PinPadCreateScreenModel(loginInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `initial state is correct`() = runTest {
        with(model.uiState.value.model) {
            assertEquals("", pin)
            assertEquals("", pinRepeat)
            assertFalse(isButtonInProgress)
        }
    }

    @Test
    fun `onPinChanged updates pin correctly`() = runTest {
        model.onPinChanged("1234")
        assertEquals("1234", model.uiState.value.model.pin)
    }

    @Test
    fun `onPinRepeatChanged updates pinRepeat correctly`() = runTest {
        model.onPinRepeatChanged("4321")
        assertEquals("4321", model.uiState.value.model.pinRepeat)
    }

    @Test
    fun `submitPin does not proceed with invalid pin size`() = runTest {
        model.onPinChanged("123") // Less than required PIN_SIZE
        model.submitPin()
        assertTrue(model.uiState.value.navigationEvents.isEmpty())
    }

    @Test
    fun `submitPin does not proceed with non-digit characters in pin`() = runTest {
        model.onPinChanged("123a")
        model.submitPin()
        assertTrue(model.uiState.value.navigationEvents.isEmpty())
    }

    @Test
    fun `submitPin with valid pin navigates to next screen`() = runTest {
        val validPin = "1234"
        everySuspending { loginInteractor.setPin(validPin) } returns Unit
        model.onPinChanged(validPin)
        model.submitPin()
        advanceTimeBy(500) // Simulate coroutine delay if needed
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogNavigate)
    }

    @Test
    fun `submitPin shows error on loginInteractor failure`() = runTest {
        val validPin = "1234"
        val errorMessage = "Error setting PIN"
        everySuspending { loginInteractor.setPin(isAny()) } runs { throw RuntimeException(errorMessage) }
        model.onPinChanged(validPin)
        model.submitPin()
        advanceTimeBy(2000) // Simulate coroutine delay if needed
        assertFalse(model.uiState.value.alertErrors.isEmpty())
    }

    @Test
    fun `onPinChanged exceeds PIN_SIZE limit trims input`() = runTest {
        model.onPinChanged("12345")
        assertEquals("1234", model.uiState.value.model.pin) // Assuming PIN_SIZE = 4
    }

    @Test
    fun `onPinRepeatChanged exceeds PIN_SIZE limit trims input`() = runTest {
        model.onPinRepeatChanged("123456")
        assertEquals("1234", model.uiState.value.model.pinRepeat) // Assuming PIN_SIZE = 4
    }

    @Test
    fun `isButtonInProgress resets after submitPin completion`() = runTest {
        val validPin = "1234"
        model.onPinChanged(validPin)
        model.onPinRepeatChanged(validPin)

        everySuspending { loginInteractor.setPin(validPin) } returns Unit

        model.submitPin()
        advanceTimeBy(500) // Ensure coroutine has completed
        assertFalse(model.uiState.value.model.isButtonInProgress)
    }
}
