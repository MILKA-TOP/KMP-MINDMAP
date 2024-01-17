package ru.lipt.testing.edit.question.base.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class TableFieldModel {
    data class Header(val text: String) : TableFieldModel()
    data class Caption(val text: String) : TableFieldModel()

    data class HeaderEdit(val text: String) : TableFieldModel()

    data class SingleCheckboxSelect(
        val text: String,
        val isSelected: Boolean = false,
        val enabled: Boolean = true,
        val resultType: AnswerResultType = AnswerResultType.NONE,
    ) : TableFieldModel()

    data class MultipleCheckboxSelect(
        val text: String,
        val isSelected: Boolean = false,
        val enabled: Boolean = true,
        val resultType: AnswerResultType = AnswerResultType.NONE,
    ) : TableFieldModel()

    data class SingleCheckboxEdit(val text: String = "", val isSelected: Boolean = false) : TableFieldModel()
    data class MultipleCheckboxEdit(val text: String = "", val isSelected: Boolean = false) : TableFieldModel()

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
