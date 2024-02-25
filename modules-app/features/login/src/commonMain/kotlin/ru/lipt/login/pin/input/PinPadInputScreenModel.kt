package ru.lipt.login.pin.input

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
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

    private var _pin: String = ""

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onPinChanged(pin: String) {
        _pin = pin
        _uiState.updateUi { copy(pin = pin.take(PIN_SIZE)) }
    }

    fun onSubmitPinButtonClick() {
        val pin = _pin
        if (pin.length != PIN_SIZE) return

        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(isSetButtonInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(isSetButtonInProgress = true) }
            delay(LOADING_DELAY)

            loginInteractor.login(pin)
            _uiState.navigateTo(NavigationTarget.CatalogScreenNavigate)
        }
    }

    fun onLogoutButtonClick() {
        _uiState.updateUi { copy(showLogOutAlert = true) }
    }

    fun onConfirmLogOutAlertButtonClick() {
        onCloseLogOutAlert()
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(isLogOutButtonInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(isLogOutButtonInProgress = true) }
            delay(LOADING_DELAY)
            loginInteractor.logout()
            _uiState.navigateTo(NavigationTarget.HelloScreenNavigate)
        }
    }

    fun onCloseLogOutAlert() {
        _uiState.updateUi { copy(showLogOutAlert = false) }
    }

    private companion object {
        const val LOADING_DELAY = 250L
    }
}
