package ru.lipt.login.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.login.LoginInteractor

class SplashScreenModel(
    private val loginInteractor: LoginInteractor
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<Unit, NavigationTarget> =
        MutableScreenUiStateFlow(Unit)
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    init {
        screenModelScope.launchCatching {
            delay(SPLASH_DELAY)
            if (loginInteractor.containsSavedAuthData()) {
                _uiState.navigateTo(NavigationTarget.PinInputScreenNavigate)
            } else {
                _uiState.navigateTo(NavigationTarget.HelloScreenNavigate)
            }
        }
    }

    private companion object {
        const val SPLASH_DELAY = 5_500L
    }
}
