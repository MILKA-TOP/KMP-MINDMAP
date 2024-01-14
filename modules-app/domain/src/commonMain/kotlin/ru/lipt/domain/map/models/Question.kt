package ru.lipt.domain.map.models

data class Question(
    val id: String,
    val nodeId: String,
    val questionText: String,
    val type: QuestionType,
    val answers: List<Answer>
)
