package ru.lipt.core.compose.alert

interface AlertErrorHolder {
    val alertErrors: List<UiError.Alert>
    val alertError: UiError.Alert?
    fun List<UiError.Alert>.consumeAlert(): List<UiError.Alert>
}

class AlertErrorHolderImpl(override val alertErrors: List<UiError.Alert>) : AlertErrorHolder {

    override val alertError: UiError.Alert? get() = alertErrors.firstOrNull()

    override fun List<UiError.Alert>.consumeAlert(): List<UiError.Alert> = this.drop(1)
}
