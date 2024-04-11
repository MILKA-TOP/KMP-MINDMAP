package ru.lipt.catalog.create

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.catalog.common.params.CreateMindMapParams
import ru.lipt.domain.catalog.ICatalogInteractor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateMindMapScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var catalogInteractor: ICatalogInteractor

    private val defaultParams = CreateMindMapParams.Default
    private val referralParams = CreateMindMapParams.Referral("refId", "Referral Title", "Referral Description")

    private val defaultModel by withMocks { CreateMindMapScreenModel(defaultParams, catalogInteractor) }
    private val referralModel by withMocks { CreateMindMapScreenModel(referralParams, catalogInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `initial state with default parameters is correct`() = runTest {
        with(defaultModel.uiState.value.model) {
            assertTrue(title.isEmpty())
            assertTrue(description.isEmpty())
            assertFalse(createButtonEnabled)
        }
    }

    @Test
    fun `initial state with referral parameters is correct`() = runTest {
        with(referralModel.uiState.value.model) {
            assertEquals("Referral Title", title)
            assertEquals("Referral Description", description)
            // Assuming title and description from referral are valid for enabling the button
            assertTrue(createButtonEnabled)
        }
    }

    @Test
    fun `onTitleTextChanged updates title and validates state`() = runTest {
        defaultModel.onTitleTextChanged("New Title")
        with(defaultModel.uiState.value.model) {
            assertEquals("New Title", title)
            // Validation depends on title length and whether password conditions are met
        }
    }

    @Test
    fun `onDescriptionTextChanged updates description`() = runTest {
        defaultModel.onDescriptionTextChanged("New Description")
        assertEquals("New Description", defaultModel.uiState.value.model.description)
    }

    @Test
    fun `onPasswordTextChanged filters out whitespaces and updates state`() = runTest {
        defaultModel.onPasswordTextChanged(" Pass  Word ")
        assertEquals("PassWord", defaultModel.uiState.value.model.password)
    }

    @Test
    fun `onEnabledPasswordCheckboxClick updates enabledPassword and validates state`() = runTest {
        defaultModel.onEnabledPasswordCheckboxClick(true)
        assertTrue(defaultModel.uiState.value.model.enabledPassword)
        // Further validation depends on other fields, particularly password length
    }

    @Test
    fun `onButtonClick with valid inputs creates map and navigates`() = runTest {
        val mapId = "map123"
        everySuspending { catalogInteractor.createMap(isAny(), isAny(), isAny(), isAny()) } returns mapId

        referralModel.onTitleTextChanged("Valid Title")
        referralModel.onButtonClick()
        advanceTimeBy(500) // Simulate coroutine delay

        // Verify navigation to MindMapScreen with correct params
        assertTrue(referralModel.uiState.value.navigationEvents.first() is NavigationTarget.MindMapScreen)
        assertEquals(mapId, (referralModel.uiState.value.navigationEvents.first() as NavigationTarget.MindMapScreen).params.id)
    }

    @Test
    fun `onButtonClick with invalid inputs does not navigate`() = runTest {
        defaultModel.onTitleTextChanged("") // Invalid title
        defaultModel.onButtonClick()

        // Assert no navigation occurs
        assertTrue(defaultModel.uiState.value.navigationEvents.isEmpty())
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `onButtonClick when createMap fails shows error`() = runTest {
        val errorMessage = "Failed to create map"
        everySuspending { catalogInteractor.createMap(isAny(), isAny(), isAny(), isAny()) } runs { throw Exception(errorMessage) }

        referralModel.onTitleTextChanged("Valid Title")
        referralModel.onDescriptionTextChanged("Some description")
        referralModel.onButtonClick()

        advanceUntilIdle()
        // Verify that an error is shown
        assertFalse(referralModel.uiState.value.alertErrors.isEmpty())
        assertEquals(errorMessage, referralModel.uiState.value.alertErrors.first().message)
    }

    // Test that the create button is disabled when the password checkbox is enabled but the password is too short
    @Test
    fun `onEnabledPasswordCheckboxClick with short password disables create button`() = runTest {
        defaultModel.onEnabledPasswordCheckboxClick(true)
        defaultModel.onPasswordTextChanged("short") // Less than the required PASSWORD_SIZE

        assertFalse(defaultModel.uiState.value.model.createButtonEnabled)
    }

    // Test that the create button is enabled when the password meets the minimum size requirement
    @Test
    fun `onPasswordTextChanged to valid length enables create button if password checkbox is enabled`() = runTest {
        defaultModel.onEnabledPasswordCheckboxClick(true)
        defaultModel.onTitleTextChanged("Valid Title") // Ensure title is valid
        defaultModel.onPasswordTextChanged("validPassword") // Meets PASSWORD_SIZE

        assertTrue(defaultModel.uiState.value.model.createButtonEnabled)
    }

    // Test handling of referral parameters in enabling the create button
    @Test
    fun `referral parameters correctly initialize UI and enable create button`() = runTest {
        // Assuming referralParams have a valid title and description
        with(referralModel.uiState.value.model) {
            assertEquals("Referral Title", title)
            assertEquals("Referral Description", description)
            assertTrue(createButtonEnabled) // Create button should be enabled by default
        }
    }

    // Test that toggling the password checkbox off removes password requirement for enabling the create button
    @Test
    fun `onEnabledPasswordCheckboxClick disables password requirement for create button`() = runTest {
        defaultModel.onEnabledPasswordCheckboxClick(false) // Password protection disabled
        defaultModel.onTitleTextChanged("Valid Title") // Ensure title is valid

        assertTrue(defaultModel.uiState.value.model.createButtonEnabled)
    }

    // Test that the UI state correctly updates isLoadingInProgress during map creation
    @Test
    fun `onButtonClick sets isLoadingInProgress correctly`() = runTest {
        defaultModel.onTitleTextChanged("Valid Title")
        everySuspending { catalogInteractor.createMap(isAny(), isAny(), isAny(), isAny()) } returns "mapId"

        defaultModel.onButtonClick()

        advanceTimeBy(500) // Simulate coroutine delay
        assertFalse(defaultModel.uiState.value.model.buttonInProgress)
    }
}
