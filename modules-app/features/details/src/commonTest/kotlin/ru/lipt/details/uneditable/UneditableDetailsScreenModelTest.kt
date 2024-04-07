package ru.lipt.details.uneditable

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
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.details.uneditable.models.UneditableTestResultUi
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsViewResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestsViewResponseRemote
import ru.lipt.domain.map.models.fakeNodesViewResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.domain.map.models.fakeTestResultViewResponseRemote
import ru.lipt.domain.map.models.fakeTestsViewResponseRemote
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    SummaryViewMapResponseRemote::class,
    NodesViewResponseRemote::class,
    TestsViewResponseRemote::class,
    TestResultViewResponseRemote::class
)
class UneditableDetailsScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Mock
    lateinit var mapInteractor: IMindMapInteractor
    private val paramsWithoutOtherUser = NodeDetailsScreenParams("mapId", "nodeId")
    private val paramsWithOtherUser = NodeDetailsScreenParams("mapId", "nodeId", "userID")

    private val model by withMocks { UneditableDetailsScreenModel(paramsWithoutOtherUser, mapInteractor) }
    private val modelWithOtherUser by withMocks { UneditableDetailsScreenModel(paramsWithOtherUser, mapInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `init loads data and updates UI to success state`() = runTest {
        prepareMockData()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `onMarkButtonClick updates node marked state`() = runTest {
        prepareMockData()
        everySuspending { mapInteractor.toggleNode(isAny(), isAny()) } returns true
        model.onMarkButtonClick()
        advanceUntilIdle()
        assertNotNull(model.uiState.value.model.data)
        assertTrue(model.uiState.value.model.data!!.isNodeMarked)
    }

    @Test
    fun `onTestNavigateClick triggers navigation to CompleteTest`() = runTest {
        prepareMockDataWithTestResult()
        model.onTestNavigateClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CompleteTest)
    }

    @Test
    fun `onTestResultButtonClick navigates to TestResult screen`() = runTest {
        prepareMockDataWithTestResult()
        model.onTestResultButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.TestResult)
    }

    @Test
    fun `getMap fetches map data correctly`() = runTest {
        prepareMockData()
        // Invocation happens within init(), verifying through UI state
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    // //

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `launchCatching updates UI with error alert on failure`() = runTest {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw Exception("Error fetching map") }
        model.onStarted()
        advanceUntilIdle()
        // Verify that an error alert is shown in the UI state
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    fun `UI state updates to CompleteTest when test questions are present and not mapViewType`() = runTest {
        prepareMockDataWithTestResult(isTestResult = false)

        // Assuming the mocked node data includes test questions and we're not in mapViewType
        assertTrue(model.uiState.value.model.data?.testResult is UneditableTestResultUi.CompleteTest)
    }

    @Test
    fun `onMarkButtonClick toggles mark and updates UI`() = runTest {
        prepareMockData()
        everySuspending { mapInteractor.toggleNode(isAny(), isAny()) } returns true
        model.onMarkButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model.data?.isNodeMarked == true)
        assertFalse(model.uiState.value.model.data?.isButtonInProgress == true) // Ensure button progress is stopped
    }

    @Test
    fun `onMarkButtonClick toggles unmark and updates UI`() = runTest {
        prepareMockData()
        everySuspending { mapInteractor.toggleNode(isAny(), isAny()) } returns false
        model.onMarkButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model.data?.isNodeMarked == false)
        assertFalse(model.uiState.value.model.data?.isButtonInProgress == true) // Ensure button progress is stopped
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `onMarkButtonClick handles error correctly`() = runTest {
        everySuspending { mapInteractor.toggleNode(isAny(), isAny()) } runs { throw Exception("Network Error") }
        model.onMarkButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.alertErrors.isNotEmpty())
    }

    private suspend fun TestScope.prepareMockData(isOtherUser: Boolean = false) {
        val mockNode = fakeNodesViewResponseRemote().copy(
            id = "nodeId",
            label = "Node Title",
            description = "Node Description",
            isSelected = false,
            test = null,
        )
        val mockMap = fakeSummaryViewMapResponseRemote().copy(
            id = "mapId",
            title = "Map Title",
            nodes = listOf(mockNode)
        )

        if (isOtherUser) {
            everySuspending { mapInteractor.fetchViewMap(isAny(), isAny(), isAny()) } returns mockMap
        } else {
            everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns mockMap
        }
        if (isOtherUser) {
            modelWithOtherUser.onStarted()
        } else {
            model.onStarted()
        }
        advanceUntilIdle()
    }

    // ////

    @Test
    fun `init with otherUserId loads view map data correctly`() = runTest {
        prepareMockData(isOtherUser = true)
        assertTrue(modelWithOtherUser.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `init without otherUserId loads general map data correctly`() = runTest {
        prepareMockData()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `highlightLinkPositions correctly identifies links in node description`() = runTest {
        prepareMockDataWithTestResult()
        val links = model.uiState.value.model.data?.links.orEmpty()
        assertFalse(links.isEmpty()) // Assuming the description contains at least one URL
    }

    @Test
    fun `onTestNavigateClick with otherUserId triggers navigation to CompleteTest`() = runTest {
        prepareMockDataWithTestResult(isOtherUser = true)
        modelWithOtherUser.onTestNavigateClick()
        assertTrue(modelWithOtherUser.uiState.value.navigationEvents.first() is NavigationTarget.CompleteTest)
    }

    @Test
    fun `onTestResultButtonClick with otherUserId navigates to TestResult screen`() = runTest {
        prepareMockDataWithTestResult(isOtherUser = true)
        modelWithOtherUser.onTestResultButtonClick()
        assertTrue(modelWithOtherUser.uiState.value.navigationEvents.first() is NavigationTarget.TestResult)
    }

    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `Error fetching map updates UI to error state`() = runTest {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw Exception("Error") }
        model.onStarted()
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    private suspend fun TestScope.prepareMockDataWithTestResult(isOtherUser: Boolean = false, isTestResult: Boolean = true) {
        val fakeTestResult = fakeTestResultViewResponseRemote().copy(
            message = "Well done!",
            correctQuestionsCount = 5,
        )
        val fakeTest = fakeTestsViewResponseRemote().copy(
            id = "testId",
            nodeId = "nodeId",
            questions = listOf(QuestionsViewResponseRemote("", "testId", "", QuestionType.MULTIPLE_CHOICE)),
            testResult = fakeTestResult.takeIf { isTestResult }

        )
        val mockNode = fakeNodesViewResponseRemote().copy(
            id = "nodeId",
            label = "Node Title",
            description = "Node Description, https://kosi-libs.org",
            isSelected = false,
            test = fakeTest,
            parentNodeId = "parentId"
        )
        val mockMap = fakeSummaryViewMapResponseRemote().copy(
            id = "mapId",
            title = "Map Title",
            nodes = listOf(mockNode)
        )

        if (isOtherUser) {
            everySuspending { mapInteractor.fetchViewMap(isAny(), isAny(), isAny()) } returns mockMap
        } else {
            everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns mockMap
        }
        if (isOtherUser) {
            modelWithOtherUser.onStarted()
        } else {
            model.onStarted()
        }
        advanceUntilIdle()
    }
}
