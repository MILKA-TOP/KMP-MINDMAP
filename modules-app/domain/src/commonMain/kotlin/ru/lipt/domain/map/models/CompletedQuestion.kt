package ru.lipt.domain.map.models

data class CompletedQuestion(
    val id: String,
    val nodeId: String,
    val questionText: String,
    val type: QuestionType,
    val answers: List<CompletedAnswer>
)
