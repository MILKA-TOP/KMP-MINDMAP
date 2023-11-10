package ru.lipt.core.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import ru.lipt.core.cache.CachePolicy.Type.ALWAYS
import ru.lipt.core.cache.CachePolicy.Type.CLEAR
import ru.lipt.core.cache.CachePolicy.Type.LOCAL
import ru.lipt.core.cache.CachePolicy.Type.NEVER
import ru.lipt.core.cache.CachePolicy.Type.REFRESH

/**
 * Request must be a data class because it's used as a cache key
 */
open class CachePolicyRepository<Request : Any, Response>(
    private val localDataSource: LocalDataSource<Request, CacheEntry<Request, Response>>,
    private val remoteDataSource: RemoteDataSource<Request, Response>,
) {

    private val mutex = Mutex()

    fun observe(request: Request): Flow<Response?> =
        localDataSource.observe(request)
            .map { it?.value }

    suspend fun fetch(request: Request, cachePolicy: CachePolicy, useLock: Boolean = false): Response? =
        withOptionalLock(useLock) {
            when (cachePolicy.type) {
                NEVER -> remoteDataSource.fetch(request)
                ALWAYS -> {
                    localDataSource.get(request)?.value ?: fetchAndCache(request)
                }
                CLEAR -> {
                    localDataSource.get(request)?.value.also {
                        localDataSource.remove(request)
                    }
                }
                REFRESH -> fetchAndCache(request)
                LOCAL -> localDataSource.get(request)?.value
            }
        }

    suspend fun updateCache(request: Request, update: Response.() -> Response) {
        val oldValue: Response = localDataSource.get(request)?.value ?: throw IllegalStateException("Nothing to update in cache!")
        localDataSource.set(request, CacheEntry(key = request, value = oldValue.update()))
    }

    private suspend fun fetchAndCache(request: Request): Response =
        remoteDataSource.fetch(request).also {
            localDataSource.set(request, CacheEntry(key = request, value = it))
        }

    private suspend fun <T> withOptionalLock(useLock: Boolean, block: suspend () -> T): T =
        if (useLock) mutex.withLock { block() } else block()
}

data class CacheEntry<Request, T>(
    val key: Request,
    val value: T,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
)

interface LocalDataSource<in Key : Any, Result> {
    suspend fun get(key: Key): Result?
    fun observe(key: Key): StateFlow<Result?>
    suspend fun set(key: Key, value: Result)
    suspend fun remove(key: Key)
    suspend fun clear()
}

interface RemoteDataSource<Request, Response> {
    suspend fun fetch(request: Request): Response
}
