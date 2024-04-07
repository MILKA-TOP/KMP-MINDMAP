package ru.lipt.map.ui

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
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.UserResponseRemote
import ru.lipt.domain.map.models.fakeNodesEditResponseRemote
import ru.lipt.domain.map.models.fakeNodesViewResponseRemote
import ru.lipt.domain.map.models.fakeSummaryEditMapResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.ui.models.MapScreenUi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    SummaryViewMapResponseRemote::class,
    SummaryEditMapResponseRemote::class,
    NodesViewResponseRemote::class,
    NodesEditResponseRemote::class
)
@Suppress("TooGenericExceptionThrown")
class MapScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val params = MapScreenParams("mapId")

    private val model by withMocks { MapScreenModel(params, mapInteractor) }

    private val viewMap = fakeSummaryViewMapResponseRemote().copy(
        id = "mapId",
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
        nodes = listOf(
            fakeNodesViewResponseRemote().copy(
                id = "nodeId",
                label = ""
            )
        )
    )
    private val editMap = fakeSummaryEditMapResponseRemote().copy(
        id = "mapId",
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
        nodes = listOf(
            fakeNodesEditResponseRemote().copy(
                id = "nodeId",
                label = ""
            )
        )

    )

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun reset() {
//        mocker.reset()
    }

    @Test
    fun `init loads map and updates UI to success state`() = runTest {
        fakeEditLoad()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `onCreateNewNode displays enter new node title dialog`() = runTest {
        fakeEditLoad()
        model.onCreateNewNode(MapScreenModel.ROOT_ID)
        assertTrue(model.uiState.value.model.data?.alert is MapScreenUi.EnterNewNodeTitle)
    }

    @Test
    fun `onBackButtonClick navigates up`() = runTest {
        fakeEditLoad()
        model.onBackButtonClick()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.NavigateUp)
    }

    @Test
    fun `openMapDetails navigates based on map type`() = runTest {
        fakeViewLoad()
        // Assume _mapType is initially VIEW
        model.openMapDetails()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MapDetailsViewScreenDestination)
    }

    @Test
    fun `onFieldTextChanged updates new node title in UI`() = runTest {
        fakeEditLoad()
        model.onCreateNewNode(MapScreenModel.ROOT_ID) // to initialize dialog
        val newNodeTitle = "New Node"
        model.onFieldTextChanged(newNodeTitle)
        assertEquals(newNodeTitle, (model.uiState.value.model.data?.alert as MapScreenUi.EnterNewNodeTitle).title)
    }

    @Test
    fun `onConfirm adds new node and updates UI`() = runTest {
        fakeEditLoad()
        everySuspending { mapInteractor.addNewNodeToMap(isAny(), isAny(), isAny()) } returns editMap
        model.onCreateNewNode(MapScreenModel.ROOT_ID) // Set parent node ID
        model.onFieldTextChanged("New Node Title")
        model.onConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `onCancel clears new node title and hides dialog`() = runTest {
        fakeEditLoad()
        model.onFieldTextChanged("Temporary Title")
        model.onCreateNewNode(MapScreenModel.ROOT_ID) // to initialize dialog
        model.onCancel()
        assertNull(model.uiState.value.model.data?.alert)
    }

    @Test
    fun `saveMindMap triggers save operation and updates UI on completion`() = runTest {
        fakeEditLoad()
        everySuspending { mapInteractor.updateMindMap(isAny()) } returns editMap
        model.saveMindMap()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `onEditNodeClick navigates to editable details screen`() = runTest {
        fakeEditLoad()
        val nodeId = "nodeId"
        model.onEditNodeClick(nodeId)
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.EditableDetailsScreen)
    }

    @Test
    fun `onViewNodeClick navigates to uneditable details screen`() = runTest {
        fakeViewLoad()
        val nodeId = "nodeId"
        model.onViewNodeClick(nodeId)
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.UneditableDetailsScreen)
    }

    @Test
    fun `onCreateNewNode with null parent id does not show dialog`() = runTest {
        fakeEditLoad()
        model.onCreateNewNode("unknownId")
        model.onFieldTextChanged("Unknown node title")
        model.onConfirm()
        advanceUntilIdle()
        assertNull(model.uiState.value.model.data?.alert, "Dialog should not be displayed without a parent node")
    }

    @Test
    fun `saveMindMap with error shows error alert and does not navigate`() = runTest {
        everySuspending { mapInteractor.updateMindMap(isAny()) } runs { throw Exception("Update Error") }
        model.saveMindMap()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty(), "Should show error alert on save failure")
        assertTrue(model.uiState.value.navigationEvents.isEmpty(), "Should not navigate on save failure")
    }

    private suspend fun TestScope.fakeEditLoad() {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns editMap
        model.init()
        advanceUntilIdle()
    }

    @Test
    fun `Error during map loading shows error alert and updates UI to error state`() = runTest {
        // Configure the mocked interactor to throw an exception
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw RuntimeException("Test exception") }

        model.onStarted() // Trigger the init function that includes the launchCatching block
        advanceUntilIdle() // Wait until all coroutines have completed

        // Verify that the UI state is updated to error
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    fun `Navigating to map details handles map type correctly`() = runTest {
        fakeEditLoad()

        model.openMapDetails()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MapDetailsEditScreenDestination)
    }

    @Test
    fun `onConfirm without parentNodeId does not proceed`() = runTest {
        fakeEditLoad()
        model.onConfirm()
        advanceUntilIdle()

        // Ensure no navigation or further actions have been taken
        assertTrue(model.uiState.value.navigationEvents.isEmpty())
    }

    private suspend fun TestScope.fakeViewLoad() {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns viewMap
        model.init()
        advanceUntilIdle()
    }
}
