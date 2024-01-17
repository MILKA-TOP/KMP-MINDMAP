package ru.lipt.testing.result.models

import androidx.compose.runtime.Immutable
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Immutable
sealed class QuestionResultUiModel {

    open val id: String = ""
    open val questionText: String = ""
    open val allAnswers: List<TableFieldModel> = listOf()
    open val correctAnswers: List<TableFieldModel> = listOf()

    data class SingleChoice(
        override val id: String,
        override val questionText: String = "",
        override val allAnswers: List<TableFieldModel.SingleCheckboxSelect> = emptyList(),
        override val correctAnswers: List<TableFieldModel.SingleCheckboxSelect> = emptyList(),
    ) : QuestionResultUiModel()

    data class MultipleChoice(
        override val id: String,
        override val questionText: String = "",
        override val allAnswers: List<TableFieldModel.MultipleCheckboxSelect> = emptyList(),
        override val correctAnswers: List<TableFieldModel.SingleCheckboxSelect> = emptyList(),
    ) : QuestionResultUiModel()
}
