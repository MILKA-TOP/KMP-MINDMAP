package ru.lipt.data.map

import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.MindNode

class MindMapDataSourceImpl : MindMapDataSource {

    override suspend fun fetch(request: String): MindMap = MindMap(
        id = request,
        nodes = listOf(
            MindNode(
                id = "0",
                title = "First"
            )
        )
    )
}
