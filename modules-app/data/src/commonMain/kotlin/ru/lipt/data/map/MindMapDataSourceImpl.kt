package ru.lipt.data.map

import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MindMap

class MindMapDataSourceImpl : MindMapDataSource {

    override suspend fun fetch(request: String): MindMap = MindMap(id = "0")
}
