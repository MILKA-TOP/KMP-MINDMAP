package ru.lipt.login.pin.create

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.login.hello.NavigationTarget
import ru.lipt.login.pin.create.models.PinPadCreateModel

class PinPadCreateScreenModel : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<PinPadCreateModel, NavigationTarget> =
        MutableScreenUiStateFlow(PinPadCreateModel())
    val uiState = _uiState.asStateFlow()

    fun onPinChanged(pin: String) {
        _uiState.updateUi { copy(pin = pin.take(MAX_PIN_SIZE)) }
    }

    fun onPinRepeatChanged(pin: String) {
        _uiState.updateUi { copy(pinRepeat = pin.take(MAX_PIN_SIZE)) }
    }

    fun submitPin() = Unit

    companion object {
        const val MAX_PIN_SIZE = 4
    }
}
