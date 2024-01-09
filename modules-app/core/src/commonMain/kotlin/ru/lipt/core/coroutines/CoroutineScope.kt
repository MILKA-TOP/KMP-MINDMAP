package ru.lipt.core.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("RethrowCaughtException")
inline fun CoroutineScope.launchCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline catchBlock: (Throwable) -> Unit = {},
    crossinline finalBlock: () -> Unit = {},
    crossinline tryBlock: suspend CoroutineScope.() -> Unit,
): Job = launch(context) {
    try {
        tryBlock()
    } catch (expected: CancellationException) {
        throw expected
    } catch (e: Throwable) {
        catchBlock(e)
    } finally {
        finalBlock()
    }
}
