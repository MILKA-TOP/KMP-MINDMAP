package ru.lipt.core.compose

import ru.lipt.core.LoadingState

inline fun <T, E> LoadingState<T, E>.onLoading(action: LoadingState.Loading<T, E>.() -> Unit) {
    if (this is LoadingState.Loading) {
        action(this)
    }
}

inline fun <T, E> LoadingState<T, E>.onSuccess(action: LoadingState.Success<T, E>.() -> Unit) {
    if (this is LoadingState.Success<T, E>) {
        action(this)
    }
}

inline fun <T, E> LoadingState<T, E>.onError(action: LoadingState.Error<T, E>.() -> Unit) {
    if (this is LoadingState.Error<T, E>) {
        action(this)
    }
}

inline fun <T, E> LoadingState<T, E>.onIdle(action: LoadingState.Idle<T, E>.() -> Unit) {
    if (this is LoadingState.Idle<T, E>) {
        action(this)
    }
}
