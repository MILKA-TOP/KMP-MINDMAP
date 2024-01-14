package ru.lipt.core

sealed class LoadingState<out T, out E> {

    open val data: T? = null

    open val error: E? = null

    class Idle<T, E> : LoadingState<T, E>()

    class Loading<T, E> : LoadingState<T, E>()

    class Success<T, E>(override val data: T) : LoadingState<T, E>()

    class Error<T, E>(override val error: E) : LoadingState<T, E>()

    inline fun copy(block: (T) -> @UnsafeVariance T): LoadingState<T, E> = when (this) {
        is Success<T, E> -> Success(block(data))
        else -> this
    }
}

fun <T, E> idle(): LoadingState<T, E> = LoadingState.Idle()

fun <T, E> loading(): LoadingState<T, E> = LoadingState.Loading()

fun <T, E> T.success(): LoadingState<T, E> = LoadingState.Success(this)

fun <T, E> E.error(): LoadingState<T, E> = LoadingState.Error(this)
