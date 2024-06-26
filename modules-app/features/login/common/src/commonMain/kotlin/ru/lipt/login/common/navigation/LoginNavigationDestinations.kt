package ru.lipt.login.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class LoginNavigationDestinations : ScreenProvider {

    data object HelloScreenDestination : LoginNavigationDestinations()
    data object SplashScreenDestination : LoginNavigationDestinations()
    data object PinInputScreenDestination : LoginNavigationDestinations()
}
