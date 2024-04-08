package ru.lipt.testing.result

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.core.LoadingState
import ru.lipt.domain.map.models.AnswersResultResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsResultResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.testing.common.params.TestingResultParams
import ru.lipt.testing.edit.question.base.models.AnswerResultType
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import ru.lipt.testing.result.models.QuestionResultUiModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@UsesFakes(
    TestResultViewResponseRemote::class,
    QuestionsResultResponseRemote::class,
    AnswersResultResponseRemote::class,
)
@Suppress("TooGenericExceptionThrown")
class TestingResultScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = Unit

    private val params = TestingResultParams(
        testResults = TestResultViewResponseRemote(
            correctQuestionsCount = 2,
            completedQuestions = listOf(
                QuestionsResultResponseRemote(
                    id = "q1",
                    testId = "test1",
                    questionText = "First Question",
                    type = QuestionType.SINGLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "a1",
                            questionId = "q1",
                            answerText = "Correct Answer",
                            isCorrect = true,
                            isSelected = true
                        ),
                        AnswersResultResponseRemote(
                            id = "a2",
                            questionId = "q1",
                            answerText = "Wrong Answer",
                            isCorrect = false,
                            isSelected = false
                        )
                    )
                ),
                QuestionsResultResponseRemote(
                    id = "q2",
                    testId = "test2",
                    questionText = "Second Question",
                    type = QuestionType.MULTIPLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "b1",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = true,
                            isSelected = true
                        ),
                        AnswersResultResponseRemote(
                            id = "b2",
                            questionId = "q2",
                            answerText = "Correct Option 2",
                            isCorrect = true,
                            isSelected = true
                        ),
                        AnswersResultResponseRemote(
                            id = "b3",
                            questionId = "q2",
                            answerText = "Incorrect Option",
                            isCorrect = false,
                            isSelected = false
                        )
                    )
                ),
                QuestionsResultResponseRemote(
                    id = "q3",
                    testId = "test2",
                    questionText = "Third Question",
                    type = QuestionType.SINGLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "b1",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = true,
                            isSelected = false
                        ),
                    )
                ),
                QuestionsResultResponseRemote(
                    id = "q4",
                    testId = "test2",
                    questionText = "fourth Question",
                    type = QuestionType.MULTIPLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "b1",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = true,
                            isSelected = false
                        ),
                    )
                ),
                QuestionsResultResponseRemote(
                    id = "q5",
                    testId = "test2",
                    questionText = "fifth Question",
                    type = QuestionType.SINGLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "b1",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = false,
                            isSelected = true
                        ),
                        AnswersResultResponseRemote(
                            id = "b2",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = true,
                            isSelected = false
                        )
                    )
                ),
                QuestionsResultResponseRemote(
                    id = "q6",
                    testId = "test2",
                    questionText = "Sixth Question",
                    type = QuestionType.MULTIPLE_CHOICE,
                    answers = listOf(
                        AnswersResultResponseRemote(
                            id = "b1",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = false,
                            isSelected = true,
                        ),
                        AnswersResultResponseRemote(
                            id = "b2",
                            questionId = "q2",
                            answerText = "Correct Option 1",
                            isCorrect = true,
                            isSelected = false,
                        )
                    )
                )
            ),
            message = "Well done!"
        )
    )

    private val model by withMocks { TestingResultScreenModel(params) }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `model initializes with success state and correct data`() = runTest {
        fakeLoad()
        assertTrue(model.uiState.value.model is LoadingState.Success)
    }

    @Test
    fun `correct questions count matches expected`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        assertEquals(params.testResults.completedQuestions.size, successState.data.questions.size)
    }

    @Test
    fun `single choice questions processed accurately`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val singleChoiceQuestion = successState.data.questions.first { it is QuestionResultUiModel.SingleChoice }
        assertNotNull(singleChoiceQuestion)
    }

    @Test
    fun `multiple choice questions processed accurately`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val multipleChoiceQuestion = successState.data.questions.first { it is QuestionResultUiModel.MultipleChoice }
        assertNotNull(multipleChoiceQuestion)
    }

    @Test
    fun `correct answers identified in single choice questions`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val singleChoiceQuestion =
            successState.data.questions.first { it is QuestionResultUiModel.SingleChoice } as QuestionResultUiModel.SingleChoice
        assertTrue(singleChoiceQuestion.correctAnswers.any { it.isSelected })
    }

    @Test
    fun `correct answers identified in multiple choice questions`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val multipleChoiceQuestion =
            successState.data.questions.first { it is QuestionResultUiModel.MultipleChoice } as QuestionResultUiModel.MultipleChoice
        assertTrue(multipleChoiceQuestion.correctAnswers.all { it.isSelected })
    }

    @Test
    fun `question text is displayed correctly`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        assertTrue(successState.data.questions.any { it.questionText == "First Question" })
    }

    @Test
    fun `answers text is displayed correctly`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val question = successState.data.questions.first()
        assertTrue(question.allAnswers.any {
            it is TableFieldModel.SingleCheckboxSelect && it.text == "Correct Answer"
                    || it is TableFieldModel.MultipleCheckboxSelect && it.text == "Correct Answer"
        })
    }

    @Test
    fun `single question is correct fully`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val question = successState.data.questions.first()
        assertTrue(question.isCorrectQuestion)
    }

    @Test
    fun `single question is incorrect`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val question = successState.data.questions[2]
        assertFalse(question.isCorrectQuestion)
    }

    @Test
    fun `multiple question is correct fully`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val question = successState.data.questions[1]
        assertTrue(question.isCorrectQuestion)
    }

    @Test
    fun `multiple question is incorrect`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val question = successState.data.questions[3]
        assertFalse(question.isCorrectQuestion)
    }

    @Test
    fun `single selected and correct answer is CORRECT`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val answer = successState.data.questions.first().allAnswers[0]
        assertTrue(answer is TableFieldModel.SingleCheckboxSelect)
        assertEquals(answer.resultType, AnswerResultType.CORRECT)
    }

    @Test
    fun `single unselected and correct answer is ERROR`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val answer = successState.data.questions[4].allAnswers[0]
        assertTrue(answer is TableFieldModel.SingleCheckboxSelect)
        assertEquals(answer.resultType, AnswerResultType.ERROR)
    }

    @Test
    fun `multiple selected and correct answer is CORRECT`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val answer = successState.data.questions[1].allAnswers[0]
        assertTrue(answer is TableFieldModel.MultipleCheckboxSelect)
        assertEquals(answer.resultType, AnswerResultType.CORRECT)
    }

    @Test
    fun `multiple unselected and correct answer is ERROR`() = runTest {
        fakeLoad()
        val successState = model.uiState.value.model as LoadingState.Success
        val answer = successState.data.questions[5].allAnswers[0]
        assertTrue(answer is TableFieldModel.MultipleCheckboxSelect)
        assertEquals(answer.resultType, AnswerResultType.ERROR)
    }

    private fun TestScope.fakeLoad() {
        advanceUntilIdle()
    }
}
