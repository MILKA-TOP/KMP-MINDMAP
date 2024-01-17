package ru.lipt.testing.result.models

import androidx.compose.runtime.Immutable

@Immutable
data class TestingResultUi(
    val questions: List<QuestionResultUiModel> = emptyList()
)
