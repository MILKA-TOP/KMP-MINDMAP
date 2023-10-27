package ru.lipt.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class MainNavigator : ScreenProvider {
    data object MapScreenDestination : MainNavigator()
}
