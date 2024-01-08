package ru.lipt.testing.edit

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.testing.common.params.TestEditScreenParams
import ru.lipt.testing.edit.models.TestingEditScreenUi
import ru.lipt.testing.edit.question.QuestionEditModel
import ru.lipt.testing.edit.question.QuestionEditModel.Companion.copy
import ru.lipt.testing.edit.question.base.models.TableFieldModel.Companion.onMultipleCheckboxChanged
import ru.lipt.testing.edit.question.base.models.TableFieldModel.Companion.onSingleCheckboxChanged
import ru.lipt.testing.edit.question.base.models.TableFieldModel.Companion.onTextChanged

class TestingScreenModel(
    private val params: TestEditScreenParams
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<TestingEditScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(TestingEditScreenUi())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun addQuestion() {
        _uiState.updateUi { copy(questions = questions + listOf(QuestionEditModel.SingleChoice())) }
        _uiState.navigateTo(NavigationTarget.OpenQuestions(_uiState.ui.questions.size - 1))
    }

    fun onNewItemAdd(questionPosition: Int) {
        val questions = _uiState.ui.questions.toMutableList()
        val question = questions[questionPosition]

        questions[questionPosition] = question.addNewItem()
        _uiState.updateUi { copy(questions = questions) }
    }

    fun onItemTextChanged(questionPosition: Int, itemPosition: Int, text: String) {
        val questions = _uiState.ui.questions.toMutableList()

        val question = questions[questionPosition]
        val fields = question.answers.toMutableList()
        fields[itemPosition] = fields[itemPosition].onTextChanged(text)
        questions[questionPosition] = question.copy(answers = fields)
        _uiState.updateUi { copy(questions = questions) }
    }

    fun onMultipleSelectChanged(questionPosition: Int, itemPosition: Int, boolean: Boolean) {
        val questions = _uiState.ui.questions.toMutableList()

        val question = questions[questionPosition]
        val fields = question.answers.toMutableList()
        fields[itemPosition] = fields[itemPosition].onMultipleCheckboxChanged(boolean)
        questions[questionPosition] = question.copy(answers = fields)
        _uiState.updateUi { copy(questions = questions) }
    }

    fun onSingleSelectChanged(questionPosition: Int, itemPosition: Int) {
        val questions = _uiState.ui.questions.toMutableList()

        val question = questions[questionPosition]
        val fields = question.answers.mapIndexed { index, item -> item.onSingleCheckboxChanged(itemPosition == index) }
        questions[questionPosition] = question.copy(answers = fields)
        _uiState.updateUi { copy(questions = questions) }
    }

    fun onHeaderTextChanged(questionPosition: Int, text: String) {
        val questions = _uiState.ui.questions.toMutableList()

        val question = questions[questionPosition]
        questions[questionPosition] = question.copy(questionText = text)
        _uiState.updateUi { copy(questions = questions) }
    }

    fun onGenerateQuestionButtonCLick() {
        // :TODO api connect
    }

    fun onSaveButtonClick() {
        if (validateQuestions()) {
            // TODO save
            _uiState.navigateTo(NavigationTarget.SuccessQuestionsSave)
        } else {
            _uiState.navigateTo(NavigationTarget.IncorrectQuestions)
        }
    }

    fun onIndicatorPageClick(position: Int) {
        _uiState.navigateTo(NavigationTarget.OpenQuestions(position))
    }

    private fun validateQuestions(): Boolean = _uiState.ui.questions.all(QuestionEditModel::isValidQuestion)
}
