package ru.lipt.login.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.login.common.navigation.LoginNavigationDestinations

@Composable
fun SplashContent(
    screen: Screen,
    screenModel: SplashScreenModel = screen.getScreenModel()
) {

    val uiState = screenModel.uiState.collectAsState().value
    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.HelloScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(LoginNavigationDestinations.HelloScreenDestination))
            }
            NavigationTarget.PinInputScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(LoginNavigationDestinations.PinInputScreenDestination))
            }
        }
    }

    Content()
}

@Composable
private fun Content() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("MindMap Splash Screen :)", modifier = Modifier.align(Alignment.Center))
    }
}
