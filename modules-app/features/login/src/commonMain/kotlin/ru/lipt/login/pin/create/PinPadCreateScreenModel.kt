package ru.lipt.login.pin.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.login.pin.create.models.PinPadCreateModel
import ru.lipt.login.pin.extensions.PIN_SIZE

class PinPadCreateScreenModel(
    private val loginInteractor: LoginInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<PinPadCreateModel, NavigationTarget> =
        MutableScreenUiStateFlow(PinPadCreateModel())
    val uiState = _uiState.asStateFlow()

    private var _pin = ""

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onPinChanged(pin: String) {
        _pin = pin
        _uiState.updateUi { copy(pin = pin.take(PIN_SIZE)) }
    }

    fun onPinRepeatChanged(pin: String) {
        _uiState.updateUi { copy(pinRepeat = pin.take(PIN_SIZE)) }
    }

    fun submitPin() {
        val pin = _pin
        if (pin.length != PIN_SIZE || pin.any { !it.isDigit() }) return
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(isButtonInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(isButtonInProgress = true) }

            loginInteractor.setPin(pin)
            _uiState.navigateTo(NavigationTarget.CatalogNavigate)
        }
    }
}
