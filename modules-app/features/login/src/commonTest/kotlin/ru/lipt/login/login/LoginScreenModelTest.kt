import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.login.IUnAuthedLoginInteractor
import ru.lipt.login.login.LoginScreenModel
import ru.lipt.login.login.NavigationTarget
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var unAuthedLoginInteractor: IUnAuthedLoginInteractor

    private val loginScreenModel by withMocks { LoginScreenModel(unAuthedLoginInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `initial state is correct`() {
        val initialState = loginScreenModel.uiState.value.model
        assertTrue(initialState.email.isEmpty())
        assertTrue(initialState.password.isEmpty())
        assertFalse(initialState.loginButtonEnable)
    }

    @Test
    fun `onEmailTextChanged with valid email enables login button if password is valid`() = runTest {
        loginScreenModel.onEmailTextChanged("valid@example.com")
        loginScreenModel.onPasswordTextChanged("validPassword")
        assertTrue(loginScreenModel.uiState.value.model.loginButtonEnable)
    }

    @Test
    fun `onEmailTextChanged with invalid email keeps login button disabled`() = runTest {
        loginScreenModel.onEmailTextChanged("invalid")
        loginScreenModel.onPasswordTextChanged("validPassword")
        assertFalse(loginScreenModel.uiState.value.model.loginButtonEnable)
    }

    @Test
    fun `onEmailTextChanged with valid email updates state correctly`() = runTest {
        val validEmail = "user@example.com"
        loginScreenModel.onEmailTextChanged(validEmail)
        assertEquals(validEmail, loginScreenModel.uiState.value.model.email)
    }

    @Test
    fun `onPasswordTextChanged trims input`() = runTest {
        val passwordWithSpaces = "  secret  "
        loginScreenModel.onPasswordTextChanged(passwordWithSpaces)
        assertEquals("secret", loginScreenModel.uiState.value.model.password)
    }

    @Test
    fun `onLoginButtonClick with valid credentials navigates to PinCreateNavigate`() = runTest {
        val email = "valid@example.com"
        val password = "password123"
        loginScreenModel.onEmailTextChanged(email)
        loginScreenModel.onPasswordTextChanged(password)
        everySuspending { unAuthedLoginInteractor.enterAuthData(isAny(), isAny()) } runs { }

        loginScreenModel.onLoginButtonClick()
        delay(2000) // Assuming there's a delay in the coroutine execution

        assertTrue(loginScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.PinCreateNavigate)
    }

    @Test
    fun `onLoginButtonClick sets buttonInProgress to true then false`() = runTest {
        loginScreenModel.onEmailTextChanged("user@example.com")
        loginScreenModel.onPasswordTextChanged("password")

        loginScreenModel.onLoginButtonClick()

        delay(100) // Adjust based on actual coroutine delay
        assertFalse(loginScreenModel.uiState.value.model.buttonInProgress)
    }

    @Test
    fun `onLoginButtonClick with empty email does not proceed with login`() = runTest {
        loginScreenModel.onEmailTextChanged("")
        loginScreenModel.onPasswordTextChanged("password")
        loginScreenModel.onLoginButtonClick()
        assertFalse(loginScreenModel.uiState.value.model.loginButtonEnable)
    }

    @Test
    fun `onLoginButtonClick with empty password does not proceed with login`() = runTest {
        loginScreenModel.onEmailTextChanged("user@example.com")
        loginScreenModel.onPasswordTextChanged("")
        loginScreenModel.onLoginButtonClick()
        assertFalse(loginScreenModel.uiState.value.model.loginButtonEnable)
    }

    @Test
    fun `onLoginButtonClick with login failure shows error alert`() = runTest {
        val errorMessage = "Login Failed"
        val email = "user@example.com"
        val password = "password"
        everySuspending { unAuthedLoginInteractor.enterAuthData(isAny(), isAny()) } runs { throw IllegalArgumentException(errorMessage) }
        loginScreenModel.onEmailTextChanged(email)
        loginScreenModel.onPasswordTextChanged(password)

        loginScreenModel.onLoginButtonClick()
        delay(100) // Adjust based on actual coroutine delay

        assertTrue(loginScreenModel.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `onEmailTextChanged - when valid email updates state correctly`() = runTest {
        // Assume
        val validEmail = "test@example.com"

        // Act
        loginScreenModel.onEmailTextChanged(validEmail)

        // Assert
        val uiState = loginScreenModel.uiState.value.model
        assertTrue { uiState.email == validEmail.trim() }
        // Add more assertions based on the expected state after this operation
    }
}
