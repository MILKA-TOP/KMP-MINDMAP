package ru.lipt.testing.edit.question

import ru.lipt.testing.edit.question.base.models.TableFieldModel

sealed class QuestionEditModel {

    open val questionId: String = ""
    open val questionText: String = ""
    open val answers: List<TableFieldModel> = listOf()
    val isAddAnswerButtonVisible: Boolean get() = answers.size < ANSWERS_MAX_SIZE
    open val areAnswersValid: Boolean get() = true

    abstract fun addNewItem(): QuestionEditModel

    data class SingleChoice(
        override val questionId: String,
        override val questionText: String = "",
        override val answers: List<TableFieldModel.SingleCheckboxEdit> = emptyList()
    ) : QuestionEditModel() {
        override fun addNewItem() = this.copy(answers = answers + listOf(TableFieldModel.SingleCheckboxEdit()))

        override val areAnswersValid: Boolean
            get() = answers.all { it.text.isNotBlank() } && answers.any { it.isSelected }
    }

    data class MultipleChoice(
        override val questionId: String,
        override val questionText: String = "",
        override val answers: List<TableFieldModel.MultipleCheckboxEdit> = emptyList()
    ) : QuestionEditModel() {
        override fun addNewItem() = this.copy(answers = answers + listOf(TableFieldModel.MultipleCheckboxEdit()))

        override val areAnswersValid: Boolean
            get() = answers.all { it.text.isNotBlank() } && answers.any { it.isSelected }
    }

    val isValidQuestion: Boolean get() = questionText.isNotBlank() && areAnswersValid

    companion object {
        private const val ANSWERS_MAX_SIZE = 8

        fun QuestionEditModel.copy(answers: List<TableFieldModel>) = when (this) {
            is SingleChoice -> copy(answers = answers.filterIsInstance<TableFieldModel.SingleCheckboxEdit>())
            is MultipleChoice -> copy(answers = answers.filterIsInstance<TableFieldModel.MultipleCheckboxEdit>())
        }

        fun QuestionEditModel.copy(questionText: String) = when (this) {
            is SingleChoice -> copy(questionText = questionText)
            is MultipleChoice -> copy(questionText = questionText)
        }
    }
}
