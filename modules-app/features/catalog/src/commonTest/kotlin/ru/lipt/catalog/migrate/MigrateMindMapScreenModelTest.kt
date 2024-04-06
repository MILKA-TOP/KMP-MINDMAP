import com.darkrockstudios.libraries.mpfilepicker.MPFile
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.catalog.migrate.MigrateMindMapScreenModel
import ru.lipt.catalog.migrate.NavigationTarget
import ru.lipt.domain.catalog.ICatalogInteractor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MigrateMindMapScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var catalogInteractor: ICatalogInteractor

    @Mock
    lateinit var mockFile: MPFile<Any>

    private val model by withMocks { MigrateMindMapScreenModel(catalogInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        CoroutineScope(StandardTestDispatcher()).launch {
            everySuspending { mockFile.getFileByteArray() } returns "mock file content".toByteArray()
            every { mockFile.path } returns "./mockfile.txt"
        }
    }

    @Test
    fun `initial state is correct`() = runTest {
        with(model.uiState.value.model) {
            assertFalse(createButtonEnabled)
            assertFalse(showFilePicker)
            assertEquals(null, selectedFileTitle)
        }
    }

    @Test
    fun `onFilePickerClick shows file picker`() = runTest {
        model.onFilePickerClick()
        assertTrue(model.uiState.value.model.showFilePicker)
    }

    @Test
    fun `onFileSelected updates UI state and enables button`() = runTest {
        model.onFileSelected(mockFile)
        with(model.uiState.value.model) {
            assertFalse(showFilePicker)
            assertEquals("mockfile.txt", selectedFileTitle)
            assertTrue(createButtonEnabled) // Assuming no password requirement by default
        }
    }

    @Test
    fun `onButtonClick without file does not proceed`() = runTest {
        model.onButtonClick()
        assertFalse(model.uiState.value.model.buttonInProgress)
    }

    @Test
    fun `onButtonClick with file and valid conditions migrates and navigates`() = runTest {
        model.onFileSelected(mockFile)
        val expectedMapId = "newMapId"
        everySuspending { catalogInteractor.migrate(isAny(), isAny(), isAny()) } returns expectedMapId

        model.onButtonClick()
        advanceTimeBy(500) // Adjust based on your coroutine setup

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MindMapScreen)
        assertEquals(expectedMapId, (model.uiState.value.navigationEvents.first() as NavigationTarget.MindMapScreen).params.id)
    }

    // ///

    @Test
    fun `onPasswordTextChanged updates password and validates state`() = runTest {
        model.onFileSelected(mockFile) // File selected and valid password set
        assertTrue(model.uiState.value.model.createButtonEnabled)
        model.onEnabledPasswordCheckboxClick(true) // Enable password protection
        assertFalse(model.uiState.value.model.createButtonEnabled)
        model.onPasswordTextChanged("validPassword123")
        with(model.uiState.value.model) {
            assertEquals("validPassword123", password)
            assertTrue(createButtonEnabled) // Check if button is enabled with valid password
        }
    }

    @Test
    fun `onEnabledPasswordCheckboxClick with empty password disables create button`() = runTest {
        model.onEnabledPasswordCheckboxClick(true)
        // Assuming no password has been set yet or it's invalid
        assertFalse(model.uiState.value.model.createButtonEnabled)
    }

    @Test
    fun `onEnabledPasswordCheckboxClick with valid password keeps create button disabled`() = runTest {
        model.onPasswordTextChanged("ValidPassword123")
        model.onEnabledPasswordCheckboxClick(true) // Assuming the password meets requirements
        assertFalse(model.uiState.value.model.createButtonEnabled)
    }

    @Test
    fun `onEnabledPasswordCheckboxClick with valid password and file keeps create button enabled`() = runTest {
        model.onFileSelected(mockFile) // Ensure file is selected
        model.onPasswordTextChanged("ValidPassword123")
        model.onEnabledPasswordCheckboxClick(true) // Assuming the password meets requirements
        assertTrue(model.uiState.value.model.createButtonEnabled)
    }

    @Test
    fun `onButtonClick with enabled password but no password set shows error`() = runTest {
        model.onEnabledPasswordCheckboxClick(true)
        model.onFileSelected(mockFile) // File is selected but password is not set
        model.onButtonClick()

        advanceTimeBy(500) // Ensure coroutine completes for potential error handling
        assertFalse(model.uiState.value.alertErrors.isEmpty()) // Expecting an error due to missing password
    }

    @Test
    fun `onButtonClick with enabled password and valid password proceeds to migration`() = runTest {
        val expectedMapId = "mappedId123"
        model.onEnabledPasswordCheckboxClick(true)
        model.onPasswordTextChanged("ValidPassword123")
        model.onFileSelected(mockFile) // File selected and valid password set
        everySuspending { catalogInteractor.migrate(isAny(), isAny(), isAny()) } returns expectedMapId

        model.onButtonClick()
        advanceTimeBy(500) // Simulate coroutine delay

        // Verify navigation occurs after successful migration
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MindMapScreen)
    }

    @Test
    fun `onButtonClick progress state correctly managed`() = runTest {
        model.onFileSelected(mockFile) // Ensure file is selected
        everySuspending { catalogInteractor.migrate(isAny(), isAny(), isAny()) } returns "mapId"

        model.onButtonClick()
        advanceTimeBy(500) // Wait for operation to complete
        assertFalse(model.uiState.value.model.buttonInProgress) // Button progress should be false after operation completes
    }
}
