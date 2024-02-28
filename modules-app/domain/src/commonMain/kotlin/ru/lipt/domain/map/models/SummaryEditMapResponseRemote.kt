package ru.lipt.domain.map.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

@Serializable
@SerialName("EDIT")
data class SummaryEditMapResponseRemote(
    override val id: String,
    override val title: String,
    val description: String,
    val admin: UserResponseRemote,
    val nodes: List<NodesEditResponseRemote>,
    val accessUsers: List<UserResponseRemote> = emptyList()
) : SummaryMapResponseRemote()

@Serializable
data class NodesEditResponseRemote(
    val id: String,
    val label: String,
    val priorityPosition: Int,
    val description: String = "",
    val parentNodeId: String? = null,
    val test: TestsEditResponseRemote? = null,
)

@Serializable
data class TestsEditResponseRemote(
    val id: String,
    val nodeId: String,
    val questions: List<QuestionsEditResponseRemote> = emptyList(),
)

@Serializable
data class QuestionsEditResponseRemote(
    val id: String,
    val testId: String,
    val questionText: String,
    val questionType: QuestionType,
    val answers: List<AnswersEditResponseRemote> = emptyList(),
)

@Serializable
data class AnswersEditResponseRemote(
    val id: String,
    val questionId: String,
    val answerText: String,
    val isCorrect: Boolean,
)
