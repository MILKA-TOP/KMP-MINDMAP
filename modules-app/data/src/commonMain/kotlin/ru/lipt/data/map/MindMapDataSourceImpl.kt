package ru.lipt.data.map

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.serialization.Serializable
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

class MindMapDataSourceImpl(
    private val config: ApplicationConfig,
    private val client: HttpClient,
) : MindMapDataSource {

    override suspend fun fetch(request: String): SummaryMapResponseRemote =
        client.get(
            urlString = "${config.baseUrl}/maps/fetch?mapId=$request"
        ).body()

//    override suspend fun createNewNode(mapId: String, parentId: String, title: String) = Node(
//        id = Random.nextInt().toString(),
//        parentId = parentId,
//        title = title,
//    )

    override suspend fun deleteMap(mapId: String) {
        client.post(
            urlString = "${config.baseUrl}/maps/delete?mapId=$mapId"
        )
    }

    override suspend fun eraseMap(mapId: String, type: MapRemoveType) {
        client.post(
            urlString = "${config.baseUrl}/maps/erase?mapId=$mapId&type=${type.name}"
        )
    }

    override suspend fun toggleNode(nodeId: String): Boolean =
        client.post(
            urlString = "${config.baseUrl}/nodes/toggle-selection?nodeId=$nodeId"
        ).body<NodeToggleResponseRemote>().isMarked

//    override suspend fun sendTestAnswersForNode(
//        mapId: String, nodeId: String, answers: List<RequestAnswer>
//    ): QuestionResult {
//        val question1 = CompletedQuestion(
//            id = "1", nodeId = "node1", questionText = "What is your favorite color?", type = QuestionType.SINGLE_CHOICE, answers = listOf(
//                CompletedAnswer("1", "Red", true, false),
//                CompletedAnswer("2", "Blue", false, true),
//                CompletedAnswer("3", "Green", false, false)
//            )
//        )
//
//        val question2 = CompletedQuestion(
//            id = "2", nodeId = "node2", questionText = "Select all that apply.", type = QuestionType.MULTIPLE_CHOICE, answers = listOf(
//                CompletedAnswer("4", "Option A", true, true),
//                CompletedAnswer("5", "Option B", false, false),
//                CompletedAnswer("6", "Option C", true, false)
//            )
//        )
//
//        val question3 = CompletedQuestion(
//            id = "3",
//            nodeId = "node3",
//            questionText = "What is the capital of France?",
//            type = QuestionType.SINGLE_CHOICE,
//            answers = listOf(
//                CompletedAnswer("7", "Berlin", false, false),
//                CompletedAnswer("8", "Paris", true, true),
//                CompletedAnswer("9", "London", false, false)
//            )
//        )
//
//        return QuestionResult(
//            questionsCount = answers.size,
//            correctQuestionsCount = answers.size / 2,
//            completedQuestions = listOf(question1, question2, question3)
//        )
//    }

    @Serializable
    private data class NodeToggleResponseRemote(
        val nodeId: String,
        val isMarked: Boolean,
    )
}
