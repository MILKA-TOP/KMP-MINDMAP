package ru.lipt.testing.complete.models

import androidx.compose.runtime.Immutable
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Immutable
sealed class QuestionUiModel {

    open val questionId: String = ""
    open val questionText: String = ""
    open val answers: List<TableFieldModel> = listOf()
    open val isQuestionValid: Boolean = false
    open val selectedPositions: List<Int> = emptyList()

    data class SingleChoice(
        override val questionId: String = "",
        override val questionText: String = "",
        override val answers: List<TableFieldModel.SingleCheckboxSelect> = emptyList()
    ) : QuestionUiModel() {

        override val isQuestionValid: Boolean
            get() = answers.any { it.isSelected }

        override val selectedPositions: List<Int>
            get() = answers.mapIndexed { index, answer -> if (answer.isSelected) index else null }.filterNotNull()
    }

    data class MultipleChoice(
        override val questionId: String = "",
        override val questionText: String = "",
        override val answers: List<TableFieldModel.MultipleCheckboxSelect> = emptyList()
    ) : QuestionUiModel() {

        override val isQuestionValid: Boolean
            get() = answers.any { it.isSelected }

        override val selectedPositions: List<Int>
            get() = answers.mapIndexed { index, answer -> if (answer.isSelected) index else null }.filterNotNull()
    }

    companion object {
        fun QuestionUiModel.copy(answers: List<TableFieldModel>) = when (this) {
            is QuestionUiModel.SingleChoice -> copy(answers = answers.filterIsInstance<TableFieldModel.SingleCheckboxSelect>())
            is QuestionUiModel.MultipleChoice -> copy(answers = answers.filterIsInstance<TableFieldModel.MultipleCheckboxSelect>())
        }

        fun QuestionUiModel.copy(questionText: String) = when (this) {
            is QuestionUiModel.SingleChoice -> copy(questionText = questionText)
            is QuestionUiModel.MultipleChoice -> copy(questionText = questionText)
        }
    }
}
