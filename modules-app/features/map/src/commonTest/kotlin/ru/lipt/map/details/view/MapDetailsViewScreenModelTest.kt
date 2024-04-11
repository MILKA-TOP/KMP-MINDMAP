package ru.lipt.map.details.view

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
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.UserResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.details.view.models.MapDetailsViewUi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(SummaryViewMapResponseRemote::class)
@Suppress("TooGenericExceptionThrown")
class MapDetailsViewScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val params = MapScreenParams("mapId")

    private val model by withMocks { MapDetailsViewScreenModel(params, mapInteractor) }

    private val initMap = fakeSummaryViewMapResponseRemote().copy(
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
    )

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `init loads map details successfully`() = runTest {
        fakeLoad()
        model.init()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `Error fetching map details updates UI to error state`() = runTest {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw Exception("Fetch error") }
        model.init()
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    fun `onHideButtonClick shows hide map dialog`() = runTest {
        fakeLoad()
        model.onHideButtonClick()
        assertTrue(model.uiState.value.model.data?.dialog is MapDetailsViewUi.Dialog.HideMap)
    }

    @Test
    fun `onRemoveMapClick shows remove map dialog`() = runTest {
        fakeLoad()
        model.onRemoveMapClick()
        assertTrue(model.uiState.value.model.data?.dialog is MapDetailsViewUi.Dialog.RemoveMap)
    }

    @Test
    fun `copyMapClick navigates to copy map screen`() = runTest {
        fakeLoad()
        model.copyMapClick()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CopyMap)
    }

    @Test
    fun `cancelDeletingMap hides dialog`() = runTest {
        fakeLoad()
        model.cancelDeletingMap()
        assertNull(model.uiState.value.model.data?.dialog)
    }

    @Test
    fun `hideMapAlertConfirm hides map and navigates to catalog`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.HIDE)) } returns Unit
        model.hideMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogDestination)
    }

    @Test
    fun `clearProgressMapAlertConfirm deletes map and navigates to catalog`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.DELETE)) } returns Unit
        model.clearProgressMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogDestination)
    }

    @Test
    fun `hide map error alert show`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.HIDE)) } runs { throw Exception("Fetch error") }
        model.hideMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `delete map error alert show`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.DELETE)) } runs { throw Exception("Fetch error") }
        model.clearProgressMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `hide alert errors hide map`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.HIDE)) } runs { throw Exception("Fetch error") }
        model.hideMapAlertConfirm()
        model.handleErrorAlertClose()
        assertTrue(model.uiState.value.alertErrors.isEmpty())
    }

    @Test
    fun `hide alert errors delete map`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.DELETE)) } runs { throw Exception("Fetch error") }
        model.clearProgressMapAlertConfirm()
        model.handleErrorAlertClose()
        assertTrue(model.uiState.value.alertErrors.isEmpty())
    }

    @Test
    fun `hideDialog updates UI by clearing dialog`() = runTest {
        fakeLoad()
        // Trigger any dialog state
        model.onHideButtonClick()
        // Now hide it
        model.cancelDeletingMap()
        assertNull(model.uiState.value.model.data?.dialog, "Dialog should be null after hiding")
    }

    @Test
    fun `onDeleteMapAlertConfirm triggers delete operation and navigates`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.DELETE)) } returns Unit
        model.onRemoveMapClick() // to set up remove dialog
        model.clearProgressMapAlertConfirm() // confirm delete
        advanceUntilIdle()
        // Check for navigation post-delete
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogDestination)
    }

    @Test
    fun `onHideMapAlertConfirm triggers hide operation and navigates`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.HIDE)) } returns Unit
        model.onHideButtonClick() // to set up hide dialog
        model.hideMapAlertConfirm() // confirm hide
        advanceUntilIdle()
        // Check for navigation post-hide
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CatalogDestination)
    }

    @Test
    fun `Error on hiding map updates UI to error state`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.HIDE)) } runs { throw Exception("Hide Error") }
        model.onHideButtonClick() // to set up hide dialog
        model.hideMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty(), "Error state should be reflected in UI")
    }

    @Test
    fun `Error on deleting map updates UI to error state`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.eraseMap(isAny(), isEqual(MapRemoveType.DELETE)) } runs { throw Exception("Delete Error") }
        model.onRemoveMapClick() // to set up remove dialog
        model.clearProgressMapAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty(), "Error state should be reflected in UI")
    }

    @Test
    fun `copyMapClick with empty map data navigates without title and description`() = runTest {
        model.copyMapClick()
        advanceUntilIdle()
        assertTrue(
            model.uiState.value.navigationEvents.first() is NavigationTarget.CopyMap,
            "Should navigate to CopyMap even with empty data"
        )
    }

    private suspend fun TestScope.fakeLoad() {
        val map = initMap
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns map
        model.init()
        advanceUntilIdle()
    }
}
