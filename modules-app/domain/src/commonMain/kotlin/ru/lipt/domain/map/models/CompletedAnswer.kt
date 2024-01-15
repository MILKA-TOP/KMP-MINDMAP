package ru.lipt.domain.map.models

data class CompletedAnswer(
    val id: String,
    val answerText: String,
    val isCorrect: Boolean,
    val isMarked: Boolean,
)
