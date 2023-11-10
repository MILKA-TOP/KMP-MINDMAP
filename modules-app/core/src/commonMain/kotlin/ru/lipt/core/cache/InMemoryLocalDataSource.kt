package ru.lipt.core.cache

import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

open class InMemoryLocalDataSource<Request : Any, Response> :
    LocalDataSource<Request, CacheEntry<Request, Response>> {

    private val cache = ConcurrentMutableMap<Request, MutableStateFlow<CacheEntry<Request, Response>?>>()

    override fun observe(key: Request): StateFlow<CacheEntry<Request, Response>?> =
        cache.getOrPut(key) {
            MutableStateFlow(null)
        }

    override suspend fun get(key: Request): CacheEntry<Request, Response>? =
        cache[key]?.firstOrNull()

    override suspend fun set(key: Request, value: CacheEntry<Request, Response>) {
        cache.getOrPut(key) {
            MutableStateFlow(null)
        }.value = value
    }

    override suspend fun remove(key: Request) {
        cache[key]?.value = null
    }

    override suspend fun clear() {
        cache.clear()
    }
}
