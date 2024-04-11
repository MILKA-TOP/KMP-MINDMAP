package ru.lipt.testing.edit

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
import ru.lipt.domain.map.models.AnswersEditResponseRemote
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.UserResponseRemote
import ru.lipt.domain.map.models.fakeAnswersEditResponseRemote
import ru.lipt.domain.map.models.fakeNodesEditResponseRemote
import ru.lipt.domain.map.models.fakeQuestionsEditResponseRemote
import ru.lipt.domain.map.models.fakeSummaryEditMapResponseRemote
import ru.lipt.domain.map.models.fakeTestsEditResponseRemote
import ru.lipt.testing.common.params.TestEditScreenParams
import ru.lipt.testing.edit.question.QuestionEditModel
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    SummaryEditMapResponseRemote::class,
    NodesEditResponseRemote::class,
    TestsEditResponseRemote::class,
    QuestionsEditResponseRemote::class,
    AnswersEditResponseRemote::class,
)
@Suppress("TooGenericExceptionThrown")
class TestingEditScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    private val params = TestEditScreenParams("mapId", "nodeId", "testId")

    @Mock
    lateinit var mapInteractor: IMindMapInteractor

    private val answer1 = fakeAnswersEditResponseRemote().copy(
        id = "answerId1",
        questionId = "questionId1",
        answerText = "Answer 1"
    )
    private val answer2 = fakeAnswersEditResponseRemote().copy(
        id = "answerId2",
        questionId = "questionId2",
        answerText = "Answer 2"
    )

    private val multipleQuestion = fakeQuestionsEditResponseRemote().copy(
        id = "questionId1",
        testId = "testId",
        questionText = "Some question?",
        questionType = QuestionType.MULTIPLE_CHOICE,
        answers = listOf(
            answer1, answer2
        )

    )
    private val singleQuestion = fakeQuestionsEditResponseRemote().copy(
        id = "questionId1",
        testId = "testId",
        questionText = "Some question?",
        questionType = QuestionType.SINGLE_CHOICE,
        answers = listOf(
            answer1.copy(id = "answer3"),
            answer2.copy(id = "answer4")
        )

    )

    private val test = fakeTestsEditResponseRemote().copy(
        id = "testId",
        nodeId = "nodeId",
        questions = listOf(
            multipleQuestion,
            singleQuestion,
        )
    )

    private val node = fakeNodesEditResponseRemote().copy(
        id = "nodeId",
        label = "Node label",
        test = test
    )

    private val initMap = fakeSummaryEditMapResponseRemote().copy(
        title = "Initial Map Title",
        description = "Initial Map Description",
        referralId = "inviteUid",
        admin = UserResponseRemote("adminId", "admin@example.com"),
        nodes = listOf(node),
    )

    private val model by withMocks { TestingEditScreenModel(params, mapInteractor) }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `init successfully loads test data into UI`() = runTest {
        fakeLoad()
        val uiState = model.uiState.value.model as LoadingState.Success
        assertNotNull(uiState.data.questions)
    }

    @Test
    fun `addQuestion adds a new question and navigates to it`() = runTest {
        fakeLoad()

        model.addQuestion()
        assertTrue(model.uiState.value.navigationEvents.size == 1)
        assertEquals(model.uiState.value.model.data?.questions?.size, test.questions.size + 1)
    }

    @Test
    fun `onNewItemAdd adds a new answer to a question`() = runTest {
        fakeLoad()

        model.onNewItemAdd(0)
        val answers = (model.uiState.value.model as LoadingState.Success).data.questions.first().answers
        assertEquals(3, answers.size)
    }

    @Test
    fun `onItemTextChanged updates answer text single answer`() = runTest {
        fakeLoad()

        model.onItemTextChanged(1, 0, "Updated Answer")
        val answer = (model.uiState.value.model as LoadingState.Success).data.questions[1].answers.first()
        assertTrue(answer is TableFieldModel.SingleCheckboxEdit)
        assertEquals("Updated Answer", answer.text)
    }

    @Test
    fun `onItemTextChanged updates answer text multiple answer`() = runTest {
        fakeLoad()

        model.onItemTextChanged(0, 0, "Updated Answer")
        advanceUntilIdle()
        val answer = (model.uiState.value.model as LoadingState.Success).data.questions.first().answers.first()
        assertTrue(answer is TableFieldModel.MultipleCheckboxEdit)
        assertEquals("Updated Answer", answer.text)
    }

    @Test
    fun `onSingleSelectChanged toggles answer correctness single answer`() = runTest {
        fakeLoad()

        model.onSingleSelectChanged(1, 0)
        val answer = (model.uiState.value.model as LoadingState.Success).data.questions[1].answers.first()
        assertTrue(answer is TableFieldModel.SingleCheckboxEdit)
        assertTrue(answer.isSelected)
    }

    @Test
    fun `onMultipleSelectChanged toggles answer correctness multiple answer`() = runTest {
        fakeLoad()

        model.onMultipleSelectChanged(0, 0, true)
        val answer = (model.uiState.value.model as LoadingState.Success).data.questions.first().answers.first()
        assertTrue(answer is TableFieldModel.MultipleCheckboxEdit)
        assertTrue(answer.isSelected)
    }

    @Test
    fun `onSaveButtonClick sends updated questions and navigates on success`() = runTest {
        fakeLoad()

        everySuspending { mapInteractor.updateQuestions(isAny(), isAny(), isAny(), isAny()) } returns Unit
        model.onSaveButtonClick()
        advanceUntilIdle()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.SuccessQuestionsSave)
    }

    @Test
    fun `onCloseAlert clears any open alert`() = runTest {
        fakeLoad()

        model.onGenerateButtonClick() // This should open an alert
        model.onCloseAlert()
        assertNull((model.uiState.value.model as LoadingState.Success).data.alert)
    }

    @Test
    fun `init with null testId loads default question model`() = runTest {
        val model = TestingEditScreenModel(params.copy(testId = null), mapInteractor)
        fakeNullLoad()

        assertTrue(model.uiState.value.model is LoadingState.Success)
        val uiState = model.uiState.value.model as LoadingState.Success
        assertTrue(uiState.data.questions.first() is QuestionEditModel.SingleChoice, "Default question list")
    }

    @Test
    fun `onHeaderTextChanged updates the question header`() = runTest {
        fakeLoad()

        val newText = "Updated Question"
        model.onHeaderTextChanged(0, newText)
        assertEquals(newText, model.uiState.value.model.data?.questions?.first()?.questionText)
    }

    @Test
    fun `onGenerateConfirm updates questions from generated test`() = runTest {
        fakeLoad()
        val updatedQuestions = test.questions + fakeQuestionsEditResponseRemote()
        val generatedTest = test.copy(questions = updatedQuestions)
        everySuspending { mapInteractor.generateTest(isAny(), isAny()) } returns generatedTest
        model.onGenerateConfirm()
        advanceUntilIdle()
        assertEquals(updatedQuestions.size, model.uiState.value.model.data?.questions?.size)
    }

    @Test
    fun `onGenerateConfirm error`() = runTest {
        fakeLoad()
        everySuspending { mapInteractor.generateTest(isAny(), isAny()) } runs { throw RuntimeException() }
        model.onGenerateConfirm()
        advanceUntilIdle()
        assertTrue(model.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `onIndicatorPageClick navigates to question detail`() = runTest {
        fakeLoad()
        val questionPosition = 0
        model.onIndicatorPageClick(questionPosition)
        assertTrue(model.uiState.value.navigationEvents.any { it is NavigationTarget.OpenQuestions && it.position == questionPosition })
    }

    @Test
    fun `updateFieldType multiple changes question type and resets answers correctness`() = runTest {
        fakeLoad()
        model.updateFieldType(0, FieldTypes.MULTIPLE)
        assertTrue(model.uiState.value.model.data?.questions?.first() is QuestionEditModel.MultipleChoice)
        assertTrue(model.uiState.value.model.data?.questions?.first()?.answers?.all {
            it is TableFieldModel.MultipleCheckboxEdit && !it.isSelected
        } == true)
    }

    @Test
    fun `updateFieldType single changes question type and resets answers correctness`() = runTest {
        fakeLoad()
        model.updateFieldType(0, FieldTypes.SINGLE)
        assertTrue(model.uiState.value.model.data?.questions?.first() is QuestionEditModel.SingleChoice)
        assertTrue(model.uiState.value.model.data?.questions?.first()?.answers?.all {
            it is TableFieldModel.SingleCheckboxEdit && !it.isSelected
        } == true)
    }

    @Test
    fun `onCloseClick prepares to remove a question`() = runTest {
        fakeLoad()
        model.onCloseClick(0)
        model.onRemoveQuestion()
        advanceUntilIdle()
        assertEquals(test.questions.size - 1, model.uiState.value.model.data?.questions?.size)
    }

    @Test
    fun `onRemoveQuestion removes the specified question`() = runTest {
        fakeNullLoad()
        model.onCloseClick(0)
        model.onRemoveQuestion()
        advanceUntilIdle()
        assertEquals(1, model.uiState.value.model.data?.questions?.size)
    }

    @Test
    fun `onRemoveAnswer removes the specified answer from a question`() = runTest {
        fakeLoad()
        val initialAnswerCount = multipleQuestion.answers.size
        model.onRemoveAnswer(0, 0) // Remove first answer of the first question
        advanceUntilIdle()
        assertEquals(initialAnswerCount - 1, model.uiState.value.model.data?.questions?.first()?.answers?.size)
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
}
