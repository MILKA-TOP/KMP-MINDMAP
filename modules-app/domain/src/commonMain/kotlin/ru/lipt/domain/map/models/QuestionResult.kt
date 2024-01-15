package ru.lipt.domain.map.models

data class QuestionResult(
    val questionsCount: Int,
    val correctQuestionsCount: Int,
    val message: String? = null,
    val completedQuestions: List<CompletedQuestion>
)
