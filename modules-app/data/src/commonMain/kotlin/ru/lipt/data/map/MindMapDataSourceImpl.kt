package ru.lipt.data.map

import ru.lipt.domain.login.models.User
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MapType
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.Node
import ru.lipt.domain.map.models.QuestionResult
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
                result = QuestionResult(
                    questionsCount = 4,
                    correctQuestionsCount = 8,
                    message = "Тест устарел",
                    completedQuestions = listOf(),
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
}
