package ru.lipt.testing.edit.models

import androidx.compose.runtime.Immutable
import ru.lipt.testing.edit.question.QuestionEditModel

@Immutable
data class TestingEditScreenUi(
    val nodeTitle: String = "",
    val isButtonEnabled: Boolean = false,
    val isButtonInProgress: Boolean = false,
    val showAlertRemoveQuestion: Boolean = false,
    val questions: List<QuestionEditModel> = listOf(),
)
