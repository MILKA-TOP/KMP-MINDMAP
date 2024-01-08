package ru.lipt.testing.edit.models

import androidx.compose.runtime.Immutable
import ru.lipt.testing.edit.question.QuestionEditModel

@Immutable
data class TestingEditScreenUi(
    val questions: List<QuestionEditModel> = listOf(QuestionEditModel.SingleChoice()),
)
