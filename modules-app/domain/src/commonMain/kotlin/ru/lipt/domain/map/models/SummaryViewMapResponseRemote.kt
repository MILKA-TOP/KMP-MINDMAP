package ru.lipt.domain.map.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

// Specific implementations for View and Edit
@Serializable
@SerialName("INTERACT")
data class SummaryViewMapResponseRemote(
    // Properties specific to View
    override val id: String,
    override val title: String,
    val description: String,
    val referralId: String,
    val admin: UserResponseRemote,
    val nodes: List<NodesViewResponseRemote>,
) : SummaryMapResponseRemote()

@Serializable
data class NodesViewResponseRemote(
    // Properties specific to View
    val id: String,
    val label: String,
    val description: String,
    val priorityPosition: Int,
    val isSelected: Boolean,
    val parentNodeId: String? = null,
    val test: TestsViewResponseRemote? = null,
)

@Serializable
data class TestsViewResponseRemote(
    // Properties specific to View
    val id: String,
    val nodeId: String,
    val questions: List<QuestionsViewResponseRemote> = emptyList(),
    val testResult: TestResultViewResponseRemote? = null,
)

@Serializable
data class QuestionsViewResponseRemote(
    // Properties specific to View
    val id: String,
    val testId: String,
    val questionText: String,
    val questionType: QuestionType,
    val answers: List<AnswersViewResponseRemote> = emptyList(),
)

@Serializable
data class AnswersViewResponseRemote(
    // Properties specific to View
    val id: String,
    val questionId: String,
    val answerText: String,
)

@Serializable
data class TestResultViewResponseRemote(
    val correctQuestionsCount: Int,
    val completedQuestions: List<QuestionsResultResponseRemote> = emptyList(),
    val message: String? = null,
)

@Serializable
data class QuestionsResultResponseRemote(
    val id: String,
    val testId: String,
    val questionText: String,
    val type: QuestionType,
    val answers: List<AnswersResultResponseRemote> = emptyList(),
)

@Serializable
data class AnswersResultResponseRemote(
    val id: String,
    val questionId: String,
    val answerText: String,
    val isCorrect: Boolean,
    val isSelected: Boolean,
)
