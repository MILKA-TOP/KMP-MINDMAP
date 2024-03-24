package ru.lipt.testing.complete.models

import androidx.compose.runtime.Immutable

@Immutable
data class TestingCompleteScreenUi(
    val questions: List<QuestionUiModel> = emptyList(),
    val buttonInProgress: Boolean = false,
    val nodeTitle: String = "",
) {
    val isAnswerButtonEnabled: Boolean = questions.all { it.isQuestionValid }
}
