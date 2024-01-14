package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.Node

interface MindMapDataSource : RemoteDataSource<String, MindMap> {
    suspend fun createNewNode(mapId: String, parentId: String, title: String): Node
}
