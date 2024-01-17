package ru.lipt.data.map

import ru.lipt.domain.login.models.User
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.Answer
import ru.lipt.domain.map.models.CompletedAnswer
import ru.lipt.domain.map.models.CompletedQuestion
import ru.lipt.domain.map.models.MapType
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.Node
import ru.lipt.domain.map.models.Question
import ru.lipt.domain.map.models.QuestionResult
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.RequestAnswer
import kotlin.random.Random

class MindMapDataSourceImpl : MindMapDataSource {

    override suspend fun fetch(request: String): MindMap = MindMap(
        id = request,
        admin = User("root", "tmp_admin@gmail.com"),
        title = "Map title",
        viewType = MapType.REACT,
        description = "Any description of Map",
        nodes = listOf(
            Node(
                id = Random.nextInt().toString(),
                title = "Map title",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc molestie velit rutrum dapibus egestas. " +
                        "Cras tristique tristique pretium. Proin pulvinar orci ut molestie pretium. Etiam at est tincidunt, " +
                        "viverra augue vitae, pretium mauris. Suspendisse a mauris ac nunc feugiat pellentesque. Nullam molestie, " +
                        "nibh vehicula congue efficitur, enim elit sodales mi, et efficitur dui turpis ac elit. Quisque dignissim diam " +
                        "sed tincidunt sollicitudin. Nam vitae ipsum tempor, malesuada ex non, congue quam. Praesent iaculis felis urna, " +
                        "ac ultricies justo pharetra a. Fusce scelerisque eros in libero volutpat, ut mattis sapien ornare.\n",
                questions = listOf(
                    Question(
                        id = "0",
                        questionText = "Question text 1",
                        type = QuestionType.SINGLE_CHOICE,
                        answers = listOf(
                            Answer(id = "00", answerText = "Answer 0"),
                            Answer(id = "01", answerText = "Answer 1"),
                            Answer(id = "02", answerText = "Answer 2"),
                        )
                    ),
                    Question(
                        id = "0",
                        questionText = "Question text 1",
                        type = QuestionType.MULTIPLE_CHOICE,
                        answers = listOf(
                            Answer(id = "10", answerText = "Answer 0"),
                            Answer(id = "11", answerText = "Answer 1"),
                            Answer(id = "12", answerText = "Answer 2"),
                        )
                    )
                )
            )
        ),
        users = listOf(User("2", "tmp_1@gmail.com"), User("2", "tmp_2@gmail.com"))
    )

    override suspend fun createNewNode(mapId: String, parentId: String, title: String) = Node(
        id = Random.nextInt().toString(),
        parentId = parentId,
        title = title,
    )

    override suspend fun deleteMap(mapId: String) = Unit

    override suspend fun sendTestAnswersForNode(
        mapId: String,
        nodeId: String,
        answers: List<RequestAnswer>
    ): QuestionResult {
        val question1 = CompletedQuestion(
            id = "1",
            nodeId = "node1",
            questionText = "What is your favorite color?",
            type = QuestionType.SINGLE_CHOICE,
            answers = listOf(
                CompletedAnswer("1", "Red", true, false),
                CompletedAnswer("2", "Blue", false, true),
                CompletedAnswer("3", "Green", false, false)
            )
        )

        val question2 = CompletedQuestion(
            id = "2",
            nodeId = "node2",
            questionText = "Select all that apply.",
            type = QuestionType.MULTIPLE_CHOICE,
            answers = listOf(
                CompletedAnswer("4", "Option A", true, true),
                CompletedAnswer("5", "Option B", false, false),
                CompletedAnswer("6", "Option C", true, false)
            )
        )

        val question3 = CompletedQuestion(
            id = "3",
            nodeId = "node3",
            questionText = "What is the capital of France?",
            type = QuestionType.SINGLE_CHOICE,
            answers = listOf(
                CompletedAnswer("7", "Berlin", false, false),
                CompletedAnswer("8", "Paris", true, true),
                CompletedAnswer("9", "London", false, false)
            )
        )

        return QuestionResult(
            questionsCount = answers.size,
            correctQuestionsCount = answers.size / 2,
            completedQuestions = listOf(question1, question2, question3)
        )
    }
}
