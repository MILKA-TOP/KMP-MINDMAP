package ru.lipt.map.ui.view

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
import ru.lipt.domain.map.models.fakeNodesViewResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.map.common.params.MapViewScreenParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    SummaryViewMapResponseRemote::class,
    SummaryEditMapResponseRemote::class,
    NodesViewResponseRemote::class,
    NodesEditResponseRemote::class
)
@Suppress("TooGenericExceptionThrown")
class MapViewScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val params = MapViewScreenParams("mapId", "userId")

    private val model by withMocks { MapViewScreenModel(params, mapInteractor) }

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

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `init loads map details successfully`() = runTest {
        fakeViewLoad()

        assertTrue(model.uiState.value.model is LoadingState.Success, "UI should be in success state after map details are loaded")
        assertNotNull(model.uiState.value.model, "Map details should be set in UI model")
    }

    @Test
    fun `Error fetching map details shows error alert and updates UI to error state`() = runTest {
        everySuspending { mapInteractor.fetchViewMap(isAny(), isAny(), isAny()) } runs { throw Exception("Fetch error") }

        model.init()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model is LoadingState.Error, "UI should be in error state after fetch failure")
        assertFalse(model.uiState.value.alertErrors.isEmpty(), "Error alert should be shown")
    }

    @Test
    fun `onBackButtonClick navigates up`() = runTest {
        fakeViewLoad()
        model.onBackButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.NavigateUp, "Should navigate up on back button click")
    }

    @Test
    fun `onViewNodeClick navigates to uneditable node details screen`() = runTest {
        fakeViewLoad()
        val nodeId = "nodeId"
        model.onViewNodeClick(nodeId)
        advanceUntilIdle()

        assertTrue(
            model.uiState.value.navigationEvents.first() is NavigationTarget.UneditableDetailsScreen,
            "Should navigate to uneditable node details screen"
        )
    }

    @Test
    fun `successful map details load updates UI with correct data`() = runTest {
        fakeViewLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val uiData = successState.data
        assertEquals("Initial Map Title", uiData.title)
    }

    private suspend fun TestScope.fakeViewLoad() {
        everySuspending { mapInteractor.fetchViewMap(isAny(), isAny(), isAny()) } returns viewMap
        model.init()
        advanceUntilIdle()
    }
}
