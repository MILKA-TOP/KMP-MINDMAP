package ru.lipt.core.compose

import androidx.compose.runtime.Immutable
import ru.lipt.core.compose.alert.AlertErrorHolder
import ru.lipt.core.compose.alert.AlertErrorHolderImpl
import ru.lipt.core.compose.alert.UiError

@Immutable
data class UiState<Ui, Nav>(
    val model: Ui,
    val navigationEvents: List<Nav> = emptyList(),
    override val alertErrors: List<UiError.Alert> = emptyList(),
) : AlertErrorHolder by AlertErrorHolderImpl(alertErrors)
