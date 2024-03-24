package ru.lipt.testing.result

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.testing.common.params.TestingResultParams
import ru.lipt.testing.edit.question.base.models.AnswerResultType
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import ru.lipt.testing.result.models.QuestionResultUiModel
import ru.lipt.testing.result.models.TestingResultUi

class TestingResultScreenModel(
    private val params: TestingResultParams
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<TestingResultUi, Unit>, Unit> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launchCatching {
            _uiState.updateUi { loading() }

            val ui = TestingResultUi(
                questions = params.testResults.completedQuestions.map { question ->
                    when (question.type) {
                        QuestionType.SINGLE_CHOICE -> QuestionResultUiModel.SingleChoice(
                            id = question.id,
                            questionText = question.questionText,
                            isCorrectQuestion = question.answers.all { it.isCorrect && it.isSelected || !it.isSelected },
                            allAnswers = question.answers.map { answer ->
                                TableFieldModel.SingleCheckboxSelect(
                                    answerId = answer.id,
                                    text = answer.answerText,
                                    isSelected = answer.isSelected,
                                    enabled = false,
                                    resultType = when {
                                        answer.isSelected && answer.isCorrect -> AnswerResultType.CORRECT
                                        answer.isSelected && !answer.isCorrect -> AnswerResultType.ERROR
                                        else -> AnswerResultType.NONE
                                    }
                                )
                            },
                            correctAnswers = question.answers.filter { it.isCorrect }.map { answer ->
                                TableFieldModel.SingleCheckboxSelect(
                                    answerId = answer.id,
                                    text = answer.answerText,
                                    isSelected = true,
                                    enabled = false,
                                )
                            }
                        )
                        QuestionType.MULTIPLE_CHOICE -> QuestionResultUiModel.MultipleChoice(
                            id = question.id,
                            questionText = question.questionText,
                            isCorrectQuestion = question.answers.all { it.isCorrect && it.isSelected || !it.isSelected },
                            allAnswers = question.answers.map { answer ->
                                TableFieldModel.MultipleCheckboxSelect(
                                    answerId = answer.id,
                                    text = answer.answerText,
                                    isSelected = answer.isSelected,
                                    enabled = false,
                                    resultType = when {
                                        answer.isSelected && answer.isCorrect -> AnswerResultType.CORRECT
                                        answer.isSelected && !answer.isCorrect -> AnswerResultType.ERROR
                                        else -> AnswerResultType.NONE
                                    }
                                )
                            },
                            correctAnswers = question.answers.filter { it.isCorrect }.map { answer ->
                                TableFieldModel.SingleCheckboxSelect(
                                    answerId = answer.id,
                                    text = answer.answerText,
                                    isSelected = true,
                                    enabled = false,
                                )
                            }
                        )
                    }
                }
            )

            _uiState.updateUi { ui.success() }
        }
    }
}
