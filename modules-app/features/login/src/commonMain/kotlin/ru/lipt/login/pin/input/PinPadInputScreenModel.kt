package ru.lipt.login.pin.input

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.login.pin.extensions.PIN_SIZE
import ru.lipt.login.pin.input.model.PinPadInputModel

class PinPadInputScreenModel(
    private val loginInteractor: LoginInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<PinPadInputModel, NavigationTarget> =
        MutableScreenUiStateFlow(PinPadInputModel())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onPinChanged(pin: String) {
        _uiState.updateUi { copy(pin = pin.take(PIN_SIZE)) }
    }

    fun onSubmitPinButtonClick() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Ошибка при входе пина"))
            }
        ) {
            val pin = _uiState.ui.pin
            if (!_uiState.ui.isPinEnabled) throw IllegalArgumentException()
            loginInteractor.login(pin)
            _uiState.navigateTo(NavigationTarget.CatalogScreenNavigate)
        }
    }

    fun onLogoutButtonClick() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Ошибка при выходе"))
            }
        ) {
            loginInteractor.logout()
            _uiState.navigateTo(NavigationTarget.HelloScreenNavigate)
        }
    }
}
