package ru.lipt.testing.edit

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
import ru.lipt.core.uuid.randomUUID
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.AnswersEditResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.testing.common.params.TestEditScreenParams
import ru.lipt.testing.edit.models.TestingEditScreenUi
import ru.lipt.testing.edit.question.QuestionEditModel
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel

class TestingEditScreenModel(
    private val params: TestEditScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<TestingEditScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _savedQuestionPosition: Int? = null

    private var _questionsModel: MutableList<QuestionModel> = mutableListOf()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    init {
        init()
    }

    fun init() {
        screenModelScope.launchCatching(catchBlock = {
            _uiState.updateUi { Unit.error() }
        }) {
            _uiState.updateUi { loading() }
            val map = mapInteractor.getMap(params.mapId) as SummaryEditMapResponseRemote
            val node = map.nodes.first { it.id == params.nodeId }
            val nodeTitle = node.label.takeIf { node.parentNodeId != null } ?: map.title
            val testModel = updateUiByTestModel(node.test)
            _uiState.updateUi {
                TestingEditScreenUi(
                    nodeTitle = nodeTitle,
                    questions = testModel.map { it.toUi() },
                    isSaveButtonEnabled = validate(),
                ).success()
            }
        }
    }

    fun addQuestion() {
        _questionsModel.add(QuestionModel())
        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
        _uiState.navigateTo(NavigationTarget.OpenQuestions(_questionsModel.size - 1))
    }

    fun onNewItemAdd(questionPosition: Int) {
        val question = _questionsModel[questionPosition]
        val updatedQuestion = question.copy(answers = question.answers + AnswerModel())
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onItemTextChanged(questionPosition: Int, itemPosition: Int, text: String) {
        val question = _questionsModel[questionPosition]
        val answers = question.answers.toMutableList()
        answers[itemPosition] = answers[itemPosition].copy(text = text.trimStart())
        val updatedQuestion = question.copy(answers = answers)
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onMultipleSelectChanged(questionPosition: Int, itemPosition: Int, boolean: Boolean) {
        val question = _questionsModel[questionPosition]
        val answers = question.answers.toMutableList()
        answers[itemPosition] = answers[itemPosition].copy(isCorrect = boolean)
        val updatedQuestion = question.copy(answers = answers)
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onSingleSelectChanged(questionPosition: Int, itemPosition: Int) {
        val question = _questionsModel[questionPosition]
        val answers = question.answers.map { it.copy(isCorrect = false) }.toMutableList()
        answers[itemPosition] = answers[itemPosition].copy(isCorrect = true)
        val updatedQuestion = question.copy(answers = answers)
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onHeaderTextChanged(questionPosition: Int, text: String) {
        val question = _questionsModel[questionPosition]
        val updatedQuestion = question.copy(question = text.trimStart())
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onSaveButtonClick() {
        screenModelScope.launchCatching(catchBlock = { throwable ->
            _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
        }, finalBlock = {
            _uiState.updateUi { copy { it.copy(isSaveButtonInProgress = false) } }
        }) {
            _uiState.updateUi { copy { it.copy(isSaveButtonInProgress = true) } }

            val testId = params.testId ?: randomUUID()
            val questions = _questionsModel.map { question ->
                QuestionsEditResponseRemote(id = question.id,
                    testId = testId,
                    questionText = question.question.trim(),
                    questionType = question.type,
                    answers = question.answers.map { answer ->
                        AnswersEditResponseRemote(
                            id = answer.id,
                            questionId = question.id,
                            answerText = answer.text.trim(),
                            isCorrect = answer.isCorrect,
                        )
                    })
            }

            mapInteractor.updateQuestions(params.mapId, params.nodeId, testId, questions)
            _uiState.navigateTo(NavigationTarget.SuccessQuestionsSave)
        }
    }

    fun onGenerateButtonClick() {
        _uiState.updateUi { copy { it.copy(alert = TestingEditScreenUi.Alert.Generate) } }
    }

    fun onGenerateConfirm() {
        onCloseAlert()
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            }, finalBlock = {
                _uiState.updateUi { copy { it.copy(isGenerateInProgress = false) } }
            }
        ) {
            _uiState.updateUi { copy { it.copy(isGenerateInProgress = true) } }
            val test = mapInteractor.generateTest(params.mapId, params.nodeId)
            val testModel = updateUiByTestModel(test)

            _uiState.updateUi {
                copy {
                    it.copy(
                        questions = testModel.map { it.toUi() },
                        isSaveButtonEnabled = validate()
                    )
                }
            }
        }
    }

    fun onIndicatorPageClick(position: Int) {
        _uiState.navigateTo(NavigationTarget.OpenQuestions(position))
    }

    private fun QuestionModel.toUi(): QuestionEditModel {
        val type = this.type
        return when (type) {
            QuestionType.SINGLE_CHOICE -> QuestionEditModel.SingleChoice(
                questionId = this.id,
                questionText = this.question,
                answers = answers.map { answer ->
                    TableFieldModel.SingleCheckboxEdit(
                        answerId = answer.id, text = answer.text, isSelected = answer.isCorrect
                    )
                })
            QuestionType.MULTIPLE_CHOICE -> QuestionEditModel.MultipleChoice(
                questionId = this.id,
                questionText = this.question,
                answers = answers.map { answer ->
                    TableFieldModel.MultipleCheckboxEdit(
                        answerId = answer.id, text = answer.text, isSelected = answer.isCorrect
                    )
                })
        }
    }

    private fun validate(): Boolean = _questionsModel.isNotEmpty() && _questionsModel.all { question ->
        question.question.isNotEmpty() && question.answers.isNotEmpty() && question.answers.all { answer ->
            answer.text.isNotEmpty()
        } && (question.type == QuestionType.SINGLE_CHOICE && question.answers.count { it.isCorrect } == 1
                || question.type == QuestionType.MULTIPLE_CHOICE && question.answers.count { it.isCorrect } >= 1)
    }

    fun updateFieldType(position: Int, type: FieldTypes) {
        val question = _questionsModel[position]
        val updatedType = when (type) {
            FieldTypes.SINGLE -> QuestionType.SINGLE_CHOICE
            FieldTypes.MULTIPLE -> QuestionType.MULTIPLE_CHOICE
        }
        if (question.type == updatedType) return
        val answers = question.answers.map { it.copy(isCorrect = false) }.toMutableList()
        val updatedQuestion = question.copy(type = updatedType, answers = answers)
        _questionsModel[position] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onCloseClick(position: Int) {
        if (_questionsModel.size <= 1) return
        _savedQuestionPosition = position
        _uiState.updateUi { copy { it.copy(alert = TestingEditScreenUi.Alert.Remove) } }
    }

    fun onRemoveQuestion() {
        val questionPosition = _savedQuestionPosition ?: return
        screenModelScope.launchCatching {
            _questionsModel.removeAt(questionPosition)

            _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
            onCloseAlert()
        }
    }

    fun onRemoveAnswer(questionPosition: Int, itemPosition: Int) {
        val question = _questionsModel[questionPosition]
        val answers = question.answers.toMutableList()
        answers.removeAt(itemPosition)
        val updatedQuestion = question.copy(answers = answers)
        _questionsModel[questionPosition] = updatedQuestion

        _uiState.updateUi { copy { it.copy(questions = _questionsModel.map { it.toUi() }, isSaveButtonEnabled = validate()) } }
    }

    fun onCloseAlert() {
        _savedQuestionPosition = null
        _uiState.updateUi { copy { it.copy(alert = null) } }
    }

    private fun updateUiByTestModel(test: TestsEditResponseRemote?): List<QuestionModel> {
        val testModel = test?.questions?.map { question ->
            QuestionModel(id = question.id,
                type = question.questionType,
                question = question.questionText,
                answers = question.answers.map { answer ->
                    AnswerModel(
                        id = answer.id, text = answer.answerText, isCorrect = answer.isCorrect
                    )
                })
        } ?: listOf(QuestionModel())

        _questionsModel = testModel.toMutableList()
        return testModel
    }

    private data class QuestionModel(
        val id: String = randomUUID(),
        val type: QuestionType = QuestionType.SINGLE_CHOICE,
        val question: String = "",
        val answers: List<AnswerModel> = emptyList()
    )

    private data class AnswerModel(
        val id: String = randomUUID(),
        val text: String = "",
        val isCorrect: Boolean = false,
    )
}
