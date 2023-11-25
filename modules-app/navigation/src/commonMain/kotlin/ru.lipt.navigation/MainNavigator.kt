package ru.lipt.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.navigation.params.map.MapScreenParams

sealed class MainNavigator : ScreenProvider {
    data class MapScreenDestination(val params: MapScreenParams) : MainNavigator()
    object DetailsScreenDestination : MainNavigator()
}
