package ru.lipt.data.map

import ru.lipt.domain.login.models.User
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MapType
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.Node
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
            )
        )
    )

    override suspend fun createNewNode(mapId: String, parentId: String, title: String) = Node(
        id = Random.nextInt().toString(),
        parentId = parentId,
        title = title,
    )
}
