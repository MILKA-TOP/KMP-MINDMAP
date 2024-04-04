package ru.lipt.testing.edit.models

import androidx.compose.runtime.Immutable
import ru.lipt.testing.edit.question.QuestionEditModel

@Immutable
data class TestingEditScreenUi(
    val nodeTitle: String = "",
    val isSaveButtonEnabled: Boolean = false,
    val isSaveButtonInProgress: Boolean = false,
    val alert: Alert? = null,
    val isGenerateInProgress: Boolean = false,
    val questions: List<QuestionEditModel> = listOf(),
    val isGenerateButtonEnabled: Boolean = isSaveButtonEnabled || questions.size <= 1,
) {
    sealed class Alert {
        data object Remove : Alert()
        data object Generate : Alert()
    }
}
