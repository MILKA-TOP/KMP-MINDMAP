package ru.lipt.core.compose

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import ru.lipt.core.compose.alert.UiError

class MutableScreenUiStateFlow<Ui, Nav>(
    model: Ui,
    navigationEvents: List<Nav> = emptyList(),
    alertErrors: List<UiError.Alert> = emptyList(),
) : MutableStateFlow<UiState<Ui, Nav>> by MutableStateFlow(
    UiState(
        model = model,
        navigationEvents = navigationEvents,
        alertErrors = alertErrors,
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

    fun handleErrorAlertClose() {
        update { copy(alertErrors = alertErrors.consumeAlert()) }
    }

    fun showAlertError(alert: UiError.Alert) =
        showAlertError(listOf(alert))

    fun showAlertError(alert: List<UiError.Alert>) = lock.withLock {
        value = value.copy(
            alertErrors = value.alertErrors + alert.filterNot { value.alertErrors.contains(it) }
        )
    }
}
