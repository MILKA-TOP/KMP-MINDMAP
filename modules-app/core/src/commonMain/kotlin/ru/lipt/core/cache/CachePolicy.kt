package ru.lipt.core.cache

data class CachePolicy(
    val type: Type = Type.ALWAYS,
    val expires: Long = 0
) {
    enum class Type {
        NEVER, // never create a cache line for the key
        ALWAYS, // always create a cache line for the key
        REFRESH, // re-fetch (refresh) the cache line for the key
        CLEAR, // clear the cache line for the key
        LOCAL, // always get value from cache
    }

    companion object {
        val ALWAYS = CachePolicy(Type.ALWAYS)
        val REFRESH = CachePolicy(Type.REFRESH)
        val CLEAR = CachePolicy(Type.CLEAR)
        val LOCAL = CachePolicy(Type.LOCAL)
    }
}

fun Boolean.toCachePolicy(): CachePolicy = if (this) {
    CachePolicy.REFRESH
} else {
    CachePolicy.ALWAYS
}
