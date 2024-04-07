package ru.lipt.testing.complete

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
import ru.lipt.domain.map.models.AnswersViewResponseRemote
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsViewResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestsViewResponseRemote
import ru.lipt.domain.map.models.UserResponseRemote
import ru.lipt.domain.map.models.fakeAnswersViewResponseRemote
import ru.lipt.domain.map.models.fakeNodesViewResponseRemote
import ru.lipt.domain.map.models.fakeQuestionsViewResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.domain.map.models.fakeTestsViewResponseRemote
import ru.lipt.testing.common.params.TestCompleteScreenParams
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    SummaryViewMapResponseRemote::class,
    NodesViewResponseRemote::class,
    TestsViewResponseRemote::class,
    QuestionsViewResponseRemote::class,
    AnswersViewResponseRemote::class,
)
@Suppress("TooGenericExceptionThrown")
class TestingCompleteScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    private val params = TestCompleteScreenParams("mapId", "nodeId", "testId")

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val answer1 = fakeAnswersViewResponseRemote().copy(
        id = "answerId1",
        questionId = "questionId1",
        answerText = "Answer 1"
    )
    private val answer2 = fakeAnswersViewResponseRemote().copy(
        id = "answerId2",
        questionId = "questionId2",
        answerText = "Answer 2"
    )

    private val multipleQuestion = fakeQuestionsViewResponseRemote().copy(
        id = "questionId1",
        testId = "testId",
        questionText = "Some question?",
        type = QuestionType.MULTIPLE_CHOICE,
        answers = listOf(
            answer1, answer2
        )

    )
    private val singleQuestion = fakeQuestionsViewResponseRemote().copy(
        id = "questionId1",
        testId = "testId",
        questionText = "Some question?",
        type = QuestionType.SINGLE_CHOICE,
        answers = listOf(
            answer1, answer2
        )

    )

    private val test = fakeTestsViewResponseRemote().copy(
        id = "testId",
        nodeId = "nodeId",
        questions = listOf(
            multipleQuestion,
            singleQuestion,
        )
    )

    private val node = fakeNodesViewResponseRemote().copy(
        id = "nodeId",
        label = "Node label",
        test = test
    )

    private val initMap = fakeSummaryViewMapResponseRemote().copy(
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
        nodes = listOf(node),
    )

    private val model by withMocks { TestingCompleteScreenModel(params, mapInteractor) }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `init loads map and question details successfully`() = runTest {
        fakeLoad()

        assertTrue(model.uiState.value.model is LoadingState.Success)
        assertNotNull(model.uiState.value.model)
    }

    @Test
    fun `Error fetching map and question details shows error alert and updates UI to error state`() = runTest {
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } runs { throw Exception("Fetch error") }
        model.init()
        advanceUntilIdle()

        assertTrue(model.uiState.value.model is LoadingState.Error, "UI should be in error state after fetch failure")
    }

    @Test
    fun `onSaveButtonClick sends answers and navigates to result screen`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.sendTestAnswersForNode(isAny(), isAny(), isAny(), isAny()) } returns TestResultViewResponseRemote(
            correctQuestionsCount = 0,
            completedQuestions = emptyList(),
            message = "Well done!"
        )

        model.onSaveButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.Result)
    }

    @Test
    fun `onIndicatorPageClick navigates to specific question page`() = runTest {
        fakeLoad()
        val questionPosition = 0
        model.onIndicatorPageClick(questionPosition)
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.OpenQuestions)
    }

    @Test
    fun `onMultipleSelectChanged updates UI with new answer selections for multiple choice questions`() = runTest {
        fakeLoad()
        val questionPosition = 0
        val answerPosition = 0
        val newSelectionState = true

        model.onMultipleSelectChanged(questionPosition, answerPosition, newSelectionState)
        advanceUntilIdle()

        val updatedQuestions = model.uiState.value.model.data?.questions
        val answerField = updatedQuestions?.get(questionPosition)?.answers?.get(answerPosition)
        assertTrue(answerField is TableFieldModel.MultipleCheckboxSelect)
        assertEquals(answerField.isSelected, newSelectionState, "Multiple choice answer selection should be updated in the UI")
    }

    @Test
    fun `onSingleSelectChanged updates UI with new answer selection for single choice questions`() = runTest {
        fakeLoad()
        val questionPosition = 1
        val answerPosition = 0

        model.onSingleSelectChanged(questionPosition, answerPosition)
        advanceUntilIdle()

        val updatedQuestions = model.uiState.value.model.data?.questions
        val answerField = updatedQuestions?.get(questionPosition)?.answers?.get(answerPosition)
        assertTrue(answerField is TableFieldModel.SingleCheckboxSelect)
        assertEquals(answerField.isSelected, true, "Multiple choice answer selection should be updated in the UI")
    }

    @Test
    fun `Error on submitting answers shows error alert and keeps user on the same page`() = runTest {
        fakeLoad()
        everySuspending {
            mapInteractor.sendTestAnswersForNode(
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        } runs { throw Exception("Submission error") }

        model.onSaveButtonClick()
        advanceUntilIdle()

        assertFalse(model.uiState.value.alertErrors.isEmpty())
        assertTrue(model.uiState.value.model.data?.buttonInProgress == false)
    }

    @Test
    fun `handleErrorAlertClose clears submission error messages`() = runTest {
        fakeLoad()
        everySuspending {
            mapInteractor.sendTestAnswersForNode(
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        } runs { throw Exception("Submission error") }
        model.onSaveButtonClick()
        advanceUntilIdle()

        // Act to close error alert
        model.handleErrorAlertClose()
        advanceUntilIdle()

        // Assert that error messages are cleared
        assertTrue(model.uiState.value.alertErrors.isEmpty(), "Error alerts should be cleared after handling error alert close")
    }

    @Test
    fun `init with null test in node does not crash and shows error state`() = runTest {
        fakeNullLoad()

        assertTrue(model.uiState.value.model is LoadingState.Error, "UI should be in error state when test data is null")
    }

    @Test
    fun `onSaveButtonClick with null questions does not crash and shows error`() = runTest {
        fakeEmptyQuestionsLoad()
        model.onSaveButtonClick()
        advanceUntilIdle()

        assertFalse(model.uiState.value.alertErrors.isEmpty(), "An error alert should be displayed if there are no questions to save")
        assertTrue(model.uiState.value.navigationEvents.isEmpty(), "Should not navigate without questions to submit")
    }

    @Test
    fun `onIndicatorPageClick with invalid position does not trigger navigation`() = runTest {
        fakeLoad()
        val invalidPosition = -1
        model.onIndicatorPageClick(invalidPosition)
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.isEmpty(), "Invalid position should not trigger navigation")
    }

    private suspend fun TestScope.fakeLoad() {
        val map = initMap
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns map
        model.init()
        advanceUntilIdle()
    }

    private suspend fun TestScope.fakeNullLoad() {
        val map = initMap.copy(nodes = initMap.nodes.map { it.copy(test = null) })
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns map
        model.init()
        advanceUntilIdle()
    }

    private suspend fun TestScope.fakeEmptyQuestionsLoad() {
        val map = initMap.copy(nodes = initMap.nodes.map { it.copy(test = it.test?.copy(questions = emptyList())) })
        everySuspending { mapInteractor.getMap(isAny(), isAny()) } returns map
        model.init()
        advanceUntilIdle()
    }
}
