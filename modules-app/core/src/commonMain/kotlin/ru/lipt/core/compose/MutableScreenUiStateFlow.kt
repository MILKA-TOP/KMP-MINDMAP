package ru.lipt.core.compose

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import kotlinx.coroutines.flow.MutableStateFlow

class MutableScreenUiStateFlow<Ui, Nav>(
    model: Ui,
    navigationEvents: List<Nav> = emptyList(),
) : MutableStateFlow<UiState<Ui, Nav>> by MutableStateFlow(
    UiState(
        model = model,
        navigationEvents = navigationEvents,
    )
) {

    val lock = Lock()

    val ui get() = lock.withLock { value.model }

    inline fun update(action: UiState<Ui, Nav>.() -> UiState<Ui, Nav>) = lock.withLock {
        value = value.action()
    }

    fun navigateTo(target: Nav) = lock.withLock {
        value = value.copy(
            navigationEvents = value.navigationEvents + target
        )
    }

    inline fun updateUi(action: Ui.() -> Ui) = lock.withLock {
        value = value.copy(model = value.model.action())
    }

    fun handleNavigation(navigate: (Nav) -> Unit) = lock.withLock {
        if (value.navigationEvents.isNotEmpty()) {
            navigate(value.navigationEvents.first())
            update { copy(navigationEvents = navigationEvents.drop(1)) }
        }
    }
}
