package ru.lipt.login.pin.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.login.pin.create.models.PinPadCreateModel

class PinPadCreateScreenModel(
    private val loginInteractor: LoginInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<PinPadCreateModel, NavigationTarget> =
        MutableScreenUiStateFlow(PinPadCreateModel())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onPinChanged(pin: String) {
        _uiState.updateUi { copy(pin = pin.take(MAX_PIN_SIZE)) }
    }

    fun onPinRepeatChanged(pin: String) {
        _uiState.updateUi { copy(pinRepeat = pin.take(MAX_PIN_SIZE)) }
    }

    fun submitPin() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Ошибка при создании пина"))
            }
        ) {
            val pin = _uiState.ui.pin
            if (!_uiState.ui.isPinEnabled) throw IllegalArgumentException()
            loginInteractor.setPin(pin)
            _uiState.navigateTo(NavigationTarget.CatalogNavigate)
        }
    }

    companion object {
        const val MAX_PIN_SIZE = 4
    }
}
