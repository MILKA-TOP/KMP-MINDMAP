package ru.lipt.login.hello

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow

class HelloScreenModel : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<Unit, NavigationTarget> =
        MutableScreenUiStateFlow(Unit)
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun onLoginButtonClick() = Unit
    fun onRegistryButtonClick() = Unit
}
