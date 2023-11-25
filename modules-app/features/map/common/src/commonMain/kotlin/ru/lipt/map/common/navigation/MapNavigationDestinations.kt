package ru.lipt.map.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.map.common.params.MapScreenParams

sealed class MapNavigationDestinations : ScreenProvider {

    data class MapScreenDestination(val params: MapScreenParams) : MapNavigationDestinations()
}
