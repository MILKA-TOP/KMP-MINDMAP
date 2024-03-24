package ru.lipt.testing.complete

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.AnswerCompleteRequestRemote
import ru.lipt.domain.map.models.QuestionCompleteRequestRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsViewResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.testing.common.params.TestCompleteScreenParams
import ru.lipt.testing.common.params.TestingResultParams
import ru.lipt.testing.complete.models.QuestionUiModel
import ru.lipt.testing.complete.models.QuestionUiModel.Companion.copy
import ru.lipt.testing.complete.models.TestingCompleteScreenUi
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import ru.lipt.testing.edit.question.base.models.TableFieldModel.Companion.onMultipleCheckboxChanged
import ru.lipt.testing.edit.question.base.models.TableFieldModel.Companion.onSingleCheckboxChanged

@Suppress("UnusedPrivateMember")
class TestingCompleteScreenModel(
    private val params: TestCompleteScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<TestingCompleteScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _questions: List<QuestionsViewResponseRemote> = emptyList()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    init {
        init()
    }

    fun init() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { Unit.error() }
            }
        ) {
            _uiState.updateUi { loading() }
            val map = mapInteractor.getMap(params.mapId) as SummaryViewMapResponseRemote
            val node = map.nodes.first { it.id == params.nodeId }
            _questions = node.test?.questions!!

            _uiState.updateUi {
                TestingCompleteScreenUi(
                    nodeTitle = node.label,
                    questions = _questions.map { question ->
                        when (question.type) {
                            QuestionType.SINGLE_CHOICE -> QuestionUiModel.SingleChoice(
                                questionId = question.id,
                                questionText = question.questionText,
                                answers = question.answers.map { TableFieldModel.SingleCheckboxSelect(it.id, it.answerText) }
                            )
                            QuestionType.MULTIPLE_CHOICE -> QuestionUiModel.MultipleChoice(
                                questionId = question.id,
                                questionText = question.questionText,
                                answers = question.answers.map { TableFieldModel.MultipleCheckboxSelect(it.id, it.answerText) }
                            )
                        }
                    }
                ).success()
            }
        }
    }

    fun onMultipleSelectChanged(questionPosition: Int, itemPosition: Int, boolean: Boolean) {
        val questions = _uiState.ui.data?.questions?.toMutableList() ?: return

        val question = questions[questionPosition]
        val fields = question.answers.toMutableList()
        fields[itemPosition] = fields[itemPosition].onMultipleCheckboxChanged(boolean)
        questions[questionPosition] = question.copy(answers = fields)
        _uiState.updateUi { copy { it.copy(questions = questions) } }
    }

    fun onSingleSelectChanged(questionPosition: Int, itemPosition: Int) {
        val questions = _uiState.ui.data?.questions?.toMutableList() ?: return

        val question = questions[questionPosition]
        val fields = question.answers.mapIndexed { index, item -> item.onSingleCheckboxChanged(itemPosition == index) }
        questions[questionPosition] = question.copy(answers = fields)
        _uiState.updateUi { copy { it.copy(questions = questions) } }
    }

    fun onIndicatorPageClick(position: Int) {
        _uiState.navigateTo(NavigationTarget.OpenQuestions(position))
    }

    fun onSaveButtonClick() {
        val questions = _uiState.ui.data?.questions?.toMutableList() ?: return

        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy { it.copy(buttonInProgress = false) } }
            }
        ) {
            _uiState.updateUi { copy { it.copy(buttonInProgress = true) } }
            val resultQuestions = questions.mapIndexed { index, question ->
                QuestionCompleteRequestRemote(
                    questionId = question.questionId,
                    answers = questions[index].selectedPositions.map { AnswerCompleteRequestRemote(_questions[index].answers[it].id) },
                )
            }
            val userAnswersTest = TestingCompleteRequestRemote(resultQuestions)

            val result = mapInteractor.sendTestAnswersForNode(
                mapId = params.mapId,
                nodeId = params.nodeId,
                testId = params.testId,
                testAnswers = userAnswersTest
            )
            _uiState.navigateTo(
                NavigationTarget.Result(
                    TestingResultParams()
                )
            )
        }
    }
}
