package ru.lipt.testing.edit.question.base.models

import androidx.compose.runtime.Immutable
import ru.lipt.core.uuid.randomUUID

@Immutable
sealed class TableFieldModel {
    data class Header(val text: String) : TableFieldModel()
    data class QuestionResultHeader(val text: String, val isCorrect: Boolean) : TableFieldModel()
    data class Caption(val text: String) : TableFieldModel()

    data class HeaderEdit(val text: String) : TableFieldModel()
    data class SelectQuestionType(val type: FieldTypes = FieldTypes.SINGLE) : TableFieldModel()

    data class SingleCheckboxSelect(
        val answerId: String,
        val text: String,
        val isSelected: Boolean = false,
        val enabled: Boolean = true,
        val resultType: AnswerResultType = AnswerResultType.NONE,
    ) : TableFieldModel()

    data class MultipleCheckboxSelect(
        val answerId: String,
        val text: String,
        val isSelected: Boolean = false,
        val enabled: Boolean = true,
        val resultType: AnswerResultType = AnswerResultType.NONE,
    ) : TableFieldModel()

    data class SingleCheckboxEdit(val answerId: String = randomUUID(), val text: String = "", val isSelected: Boolean = false) : TableFieldModel()
    data class MultipleCheckboxEdit(val answerId: String = randomUUID(), val text: String = "", val isSelected: Boolean = false) : TableFieldModel()

    data object NewItem : TableFieldModel()

    companion object {
        fun TableFieldModel.onTextChanged(text: String) = when (this) {
            is SingleCheckboxEdit -> this.copy(text = text)
            is MultipleCheckboxEdit -> this.copy(text = text)
            is HeaderEdit -> this.copy(text = text)
            else -> this
        }

        fun TableFieldModel.onSingleCheckboxChanged(boolean: Boolean) = when (this) {
            is SingleCheckboxSelect -> this.copy(isSelected = boolean)
            is SingleCheckboxEdit -> this.copy(isSelected = boolean)
            else -> this
        }

        fun TableFieldModel.onMultipleCheckboxChanged(boolean: Boolean) = when (this) {
            is MultipleCheckboxSelect -> this.copy(isSelected = boolean)
            is MultipleCheckboxEdit -> this.copy(isSelected = boolean)
            else -> this
        }
    }
}

enum class FieldTypes {
    SINGLE, MULTIPLE
}

enum class AnswerResultType {
    CORRECT, ERROR, NONE
}
