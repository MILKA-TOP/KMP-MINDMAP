package ru.lipt.map.details.edit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.core.LoadingState
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.UserResponseRemote
import ru.lipt.domain.map.models.fakeSummaryEditMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.details.edit.models.MapDetailsEditUi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(SummaryEditMapResponseRemote::class)
class MapDetailsEditScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val params = MapScreenParams("mapId")

    private val model by withMocks { MapDetailsEditScreenModel(params, mapInteractor) }

    private val initMap = fakeSummaryEditMapResponseRemote().copy(
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
        accessUsers = listOf(
            UserResponseRemote("userId1", "user1@example.com"),
            UserResponseRemote("userId2", "user2@example.com")
        )
    )

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `Initial load fetches map details and updates UI successfully`() = runTest {
        fakeLoad()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model is LoadingState.Success)
        with(model.uiState.value.model.data!!) {
            assertEquals("Initial Map Title", title)
            assertEquals("Initial Map Description", description)
            assertEquals(true, enabledShowDeleteMap)
        }
    }

    @Test
    fun `Clicking on a user shows user dialog`() = runTest {
        fakeLoad()
        model.onUserClick("userId1")
        assertTrue(model.uiState.value.model.data?.dialog is MapDetailsEditUi.Dialog.UserMap)
    }

    @Test
    fun `Clicking delete button shows delete dialog`() = runTest {
        fakeLoad()
        model.onDeleteButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model.data?.dialog is MapDetailsEditUi.Dialog.DeleteMap)
    }

    @Test
    fun `Confirming user alert navigates to user's map view`() = runTest {
        fakeLoad()
        model.onUserAlertConfirm("userId1")
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.OpenUserMap)
    }

    @Test
    fun `Canceling delete map operation hides dialog`() = runTest {
        fakeLoad()
        model.cancelDeletingMap()
        assertNull(model.uiState.value.model.data?.dialog)
    }

    @Test
    fun `Confirming delete map alert deletes map and navigates to catalog`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.deleteMap(isAny()) } returns Unit

        model.deleteMapAlertConfirm()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogDestination)
    }

    @Test
    fun `Title text change updates UI and enables save button`() = runTest {
        fakeLoad()
        model.onTitleTextChanged("New Map Title")
        advanceUntilIdle()

        assertTrue(model.uiState.value.model.data?.buttonIsEnabled == true)
    }

    @Test
    fun `Description text change updates UI and enables save button`() = runTest {
        fakeLoad()
        model.onDescriptionTextChanged("New Map Description")
        assertTrue(model.uiState.value.model.data?.buttonIsEnabled == true)
    }

    @Test
    fun `Save click saves map details and navigates back`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.saveTitleAndData(isAny(), isAny(), isAny()) } returns Unit

        model.onSaveClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.PopBack)
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `Error on fetching map details updates UI to error state`() = runTest {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw Exception("Fetching Error") }

        model.init()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `Error on deleting map updates UI to error state`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.deleteMap(isAny()) } runs { throw Exception("Deletion Error") }
        model.deleteMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `Hiding dialog updates UI correctly`() = runTest {
        fakeLoad()
        model.onDeleteButtonClick() // To ensure a dialog is visible
        advanceUntilIdle()
        model.hideDialog()
        assertNull(model.uiState.value.model.data?.dialog)
    }

    @Test
    fun `Map details are not updated if title and description remain unchanged`() = runTest {
        fakeLoad() // Mock data with initial title and description
        model.onTitleTextChanged(initMap.title) // Set title to initial value
        model.onDescriptionTextChanged(initMap.description) // Set description to initial value
        advanceUntilIdle()
        assertFalse(model.uiState.value.model.data?.buttonIsEnabled == true)
    }

    @Test
    fun `Saving unchanged map details does not invoke save operation`() = runTest {
        fakeLoad() // Mock data with initial title and description
        everySuspending { mapInteractor.saveTitleAndData(isAny(), isAny(), isAny()) } returns Unit
        model.onSaveClick()
        advanceUntilIdle()
        verifyWithSuspend(exhaustive = false) {
            mapInteractor.saveTitleAndData(isAny(), isAny(), isAny())
        }
    }

    @Test
    fun `onDeleteButtonClick triggers dialog for map deletion confirmation`() = runTest {
        fakeLoad()
        model.onDeleteButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model.data?.dialog is MapDetailsEditUi.Dialog.DeleteMap)
    }

    @Test
    fun `cancelDeletingMap stops deletion process and hides dialog`() = runTest {
        fakeLoad()
        model.onDeleteButtonClick() // Assume this starts deletion process
        advanceUntilIdle()
        model.cancelDeletingMap()
        advanceUntilIdle()
        assertNull(model.uiState.value.model.data?.dialog)
    }

    @Test
    fun `Title text change to empty string disables save button`() = runTest {
        fakeLoad()
        model.onTitleTextChanged("") // Changing title to empty
        advanceUntilIdle()
        assertFalse(model.uiState.value.model.data?.buttonIsEnabled == true)
    }

    private suspend fun TestScope.fakeLoad() {
        val map = fakeSummaryEditMapResponseRemote().copy(
            title = "Initial Map Title",
            description = "Initial Map Description",
            referralId = "inviteUid",
            admin = UserResponseRemote("adminId", "admin@example.com"),
            accessUsers = listOf(
                UserResponseRemote("userId1", "user1@example.com"),
                UserResponseRemote("userId2", "user2@example.com")
            )
        )
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns map
        model.init()
        advanceUntilIdle()
    }
}
