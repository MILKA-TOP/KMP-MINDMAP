package ru.lipt.details.editable

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
import ru.lipt.details.editable.models.EditableDetailsScreenUi
import ru.lipt.details.editable.models.EditableTestResultUi
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.fakeNodesEditResponseRemote
import ru.lipt.domain.map.models.fakeSummaryEditMapResponseRemote
import ru.lipt.domain.map.models.fakeTestsEditResponseRemote
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(SummaryEditMapResponseRemote::class, NodesEditResponseRemote::class, TestsEditResponseRemote::class)
class EditableDetailsScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Mock
    lateinit var mapInteractor: IMindMapInteractor
    private val params = NodeDetailsScreenParams("mapId1", "nodeId1")

    private val model by withMocks { EditableDetailsScreenModel(params, mapInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `initial state after load is Error`() = runTest {
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    fun `initial load fetches map and node data successfully`() = runTest {
        setupBaseFakeLoad()
        model.onStarted()

        // Assertions to validate the state after successful data loading
        assertFalse(model.uiState.value.model is LoadingState.Idle)
        advanceUntilIdle()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `initial load fetches map and node data successfully with test`() = runTest {
        setupTestFakeLoad()
        assertTrue(model.uiState.value.model.data?.testResult is EditableTestResultUi.EditTest)
    }

    @Test
    fun `initial load fetches map and node data successfully without test`() = runTest {
        setupBaseFakeLoad()
        assertTrue(model.uiState.value.model.data?.testResult is EditableTestResultUi.NoTest)
    }

    @Test
    fun `onEditTitleText updates title`() = runTest {
        setupBaseFakeLoad()

        val newTitle = "New Title"
        model.onEditTitleText(newTitle)
        assertEquals(newTitle, model.uiState.value.model.data?.title)
    }

    @Test
    fun `onEditDescriptionText updates description`() = runTest {
        setupBaseFakeLoad()

        val newDescription = "New Description"
        model.onEditDescriptionText(newDescription)
        assertEquals(newDescription, model.uiState.value.model.data?.description)
    }

    @Test
    fun `onSaveButtonClick triggers save operation and navigates on success`() = runTest {
        setupBaseFakeLoad()

        val title = "Saved Title"
        val description = "Saved Description"
        model.onEditTitleText(title)
        model.onEditDescriptionText(description)

        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isAny(), isAny()) } returns Unit

        model.onSaveButtonClick()
        advanceUntilIdle()
        // Verify navigation after successful save
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.SuccessSave)
    }

    //

    @Test
    fun `onRemoveButtonClick displays remove alert with correct title for root node`() = runTest {
        val summaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(title = "Map Title", id = "mapId1")
        val nodesEditResponseRemote = fakeNodesEditResponseRemote().copy(label = "Node title", id = "nodeId1", parentNodeId = null)
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns summaryEditMapResponseRemote
        everySuspending { mapInteractor.getEditableNode(isAny(), isAny()) } returns nodesEditResponseRemote
        model.onStarted()
        advanceUntilIdle()

        model.onRemoveButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model.data?.alertUi is EditableDetailsScreenUi.Alert.RemoveAlertUi)
        assertEquals("Map Title", (model.uiState.value.model.data?.alertUi as? EditableDetailsScreenUi.Alert.RemoveAlertUi)?.parentTitle)
    }

    @Test
    fun `onRemoveButtonClick displays remove alert with correct title for parent node`() = runTest {
        val nodesEditResponseRemote =
            fakeNodesEditResponseRemote().copy(label = "Node title", id = "nodeId1", parentNodeId = "parentNodeId")
        val parentEditNode = fakeNodesEditResponseRemote().copy(label = "Parent title", id = "parentNodeId", parentNodeId = null)
        val summaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(
            title = "Map Title",
            id = "mapId1",
            nodes = listOf(parentEditNode, nodesEditResponseRemote)
        )
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns summaryEditMapResponseRemote
        everySuspending { mapInteractor.getEditableNode(isAny(), isEqual("nodeId1")) } returns nodesEditResponseRemote
        everySuspending { mapInteractor.getEditableNode(isAny(), isEqual("parentNodeId")) } returns parentEditNode
        model.onStarted()
        advanceUntilIdle()

        model.onRemoveButtonClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model.data?.alertUi is EditableDetailsScreenUi.Alert.RemoveAlertUi)
        assertEquals("Parent title", (model.uiState.value.model.data?.alertUi as? EditableDetailsScreenUi.Alert.RemoveAlertUi)?.parentTitle)
    }

    @Test
    fun `onAlertClose clears current alert UI`() = runTest {
        setupBaseFakeLoad()
        model.onEditDescriptionText("Edited Description")
        advanceUntilIdle()

        model.onNavigateUpClick()
        advanceUntilIdle() // Simulate an alert being shown
        model.onAlertClose()
        advanceUntilIdle()

        assertNull(model.uiState.value.model.data?.alertUi)
    }

    @Test
    fun `onTestEditButtonClick navigates directly if no edits were made without test`() = runTest {
        setupBaseFakeLoad()
        // Assuming no edits to title or description
        model.onTestEditButtonClick()
        advanceUntilIdle()

        // Verify navigation to the test edit screen
        val navigationEvent = model.uiState.value.navigationEvents.first()
        assertTrue(navigationEvent is NavigationTarget.EditTest)
        assertNull(navigationEvent.params.testId)
    }

    @Test
    fun `onTestEditButtonClick navigates directly if no edits were made with test`() = runTest {
        setupTestFakeLoad()
        // Assuming no edits to title or description
        model.onTestEditButtonClick()
        advanceUntilIdle()

        // Verify navigation to the test edit screen
        val navigationEvent = model.uiState.value.navigationEvents.first()
        assertTrue(navigationEvent is NavigationTarget.EditTest)
        assertNotNull(navigationEvent.params.testId)
    }

    @Test
    fun `onTestEditButtonClick shows next and save alert if edits were made`() = runTest {
        setupBaseFakeLoad()
        // Simulate edits were made
        model.onEditTitleText("Edited Title")
        advanceUntilIdle()

        model.onTestEditButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model.data?.alertUi is EditableDetailsScreenUi.Alert.NextAndSave)
    }

    @Test
    fun `onNavigateUpClick navigates up directly if no edits were made`() = runTest {
        setupBaseFakeLoad()
        model.onNavigateUpClick()
        advanceUntilIdle()

        // Verify direct navigation
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.NavigateUp)
    }

    @Test
    fun `onNavigateUpClick shows back and save alert if edits were made`() = runTest {
        setupBaseFakeLoad()
        // Simulate edits
        model.onEditDescriptionText("Edited Description")
        advanceUntilIdle()

        model.onNavigateUpClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model.data?.alertUi is EditableDetailsScreenUi.Alert.BackAndSave)
    }

    @Test
    fun `onSaveButtonClick with editable data saves and navigates to success`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("New Title")
        model.onEditDescriptionText("New Description")

        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isAny(), isAny()) } returns Unit

        model.onSaveButtonClick()
        advanceUntilIdle()

        // Assert navigation after saving
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.SuccessSave)
    }

    @Test
    fun `onBackConfirmAlertButtonClick with editable data saves and navigates up`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("Changed Title")

        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isAny(), isAny()) } returns Unit

        model.onBackConfirmAlertButtonClick()
        advanceUntilIdle()

        // Assert navigation after save confirmation
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.NavigateUp)
    }

    @Test
    fun `onNextConfirmAlertButtonClick with editable data saves and navigates to test editing`() = runTest {
        setupBaseFakeLoad()
        model.onEditDescriptionText("Changed Description")

        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isAny(), isAny()) } returns Unit

        model.onNextConfirmAlertButtonClick()

        advanceUntilIdle()
        // Verify navigation to the test editing screen
        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.EditTest)
    }

    @Test
    fun `edit actions without change do not enable save button`() = runTest {
        val initialTitle = "Initial Title"
        setupBaseFakeLoad(initialTitle, initialTitle)
        // Simulate initial state
        model.onEditTitleText(initialTitle)
        // Reset edits to initial state
        model.onEditTitleText(initialTitle)
        advanceUntilIdle()

        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: true)
    }

    @Test
    fun `edit actions with change enable save button`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("New Title")
        model.onEditDescriptionText("New Description")

        assertTrue(model.uiState.value.model.data?.isSaveButtonEnabled ?: false)
    }

    @Test
    fun `saveButton disabled when title empty`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("")

        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: false)
    }

    @Test
    fun `saveButton disabled when title blank`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("    ")

        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: false)
    }

    @Test
    fun `reverting edits disables save button`() = runTest {
        val _initTitle = "_initTitle"
        setupBaseFakeLoad(_initTitle, _initTitle)
        model.onEditTitleText("New Title")
        // Revert change
        model.onEditTitleText(_initTitle)

        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: true)
    }

    @Test
    fun `saving with no changes does not invoke interactor`() = runTest {
        setupBaseFakeLoad()
        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isAny(), isAny()) } returns Unit
        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: true)

        model.onSaveButtonClick()
        assertFalse(model.uiState.value.model.data?.isSaveButtonEnabled ?: true)
    }

    @Test
    fun `saving with changes invokes interactor and updates state`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("Changed Title")
        everySuspending { mapInteractor.saveNodeData(isAny(), isAny(), isEqual("Changed Title"), isAny()) } returns Unit
        advanceUntilIdle()
        model.onSaveButtonClick()
        advanceUntilIdle()
        assertEquals("Changed Title", model.uiState.value.model.data?.title)
    }

    @Test
    fun `navigating up with changes prompts correct alert`() = runTest {
        setupBaseFakeLoad()
        model.onEditTitleText("Title for Navigation")
        model.onNavigateUpClick()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model.data?.alertUi is EditableDetailsScreenUi.Alert.BackAndSave)
    }

    @Test
    fun `onRemoveAlertConfirm mapId error return`() = runTest {
        model.onRemoveAlertConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.model is LoadingState.Error)
    }

    @Test
    fun `onRemoveAlertConfirm mapId success return`() = runTest {
        setupBaseFakeLoad()

        everySuspending { mapInteractor.removeNode(isAny(), isAny()) } returns Unit
        model.onRemoveAlertConfirm()
        advanceUntilIdle()
        val ui = model.uiState.value.model
        assertTrue(ui is LoadingState.Success)
        assertNull(ui.data.alertUi)
    }

    // /////
    private suspend fun TestScope.setupBaseFakeLoad(title: String = "Map title", node: String = "Node label") {
        val summaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(title = title)
        val nodesEditResponseRemote = fakeNodesEditResponseRemote().copy(label = node, parentNodeId = "anyNodeId")
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns summaryEditMapResponseRemote
        everySuspending { mapInteractor.getEditableNode(isAny(), isAny()) } returns nodesEditResponseRemote
        model.init()
        advanceUntilIdle()
    }

    private suspend fun TestScope.setupTestFakeLoad(title: String = "Map title", node: String = "Node label") {
        val summaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(title = title)
        val nodesEditResponseRemote =
            fakeNodesEditResponseRemote().copy(label = node, parentNodeId = "anyNodeId", test = fakeTestsEditResponseRemote())
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns summaryEditMapResponseRemote
        everySuspending { mapInteractor.getEditableNode(isAny(), isAny()) } returns nodesEditResponseRemote
        model.init()
        advanceUntilIdle()
    }
}
