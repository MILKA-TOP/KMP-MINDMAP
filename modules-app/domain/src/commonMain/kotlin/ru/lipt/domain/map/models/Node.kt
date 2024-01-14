package ru.lipt.domain.map.models

data class Node(
    val id: String,
    val parentId: String? = null,
    val title: String,
    val description: String = "",
    val isMarked: Boolean = false,
    val result: QuestionResult? = null,
    val questions: List<Question> = emptyList()
)
