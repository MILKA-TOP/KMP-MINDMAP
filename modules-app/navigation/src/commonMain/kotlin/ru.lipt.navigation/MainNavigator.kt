package ru.lipt.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class MainNavigator : ScreenProvider {
    data class MapScreenDestination(val id: String) : MainNavigator()
}
