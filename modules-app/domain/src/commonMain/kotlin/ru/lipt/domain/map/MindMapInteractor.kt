package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy

class MindMapInteractor(
    val mapRepository: MindMapRepository,
) {
    suspend fun getMap(id: String) = mapRepository.fetch(id, CachePolicy.ALWAYS)
}
