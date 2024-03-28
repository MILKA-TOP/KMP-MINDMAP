package ru.lipt.map.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.common.params.MapViewScreenParams

sealed class MapNavigationDestinations : ScreenProvider {

    data class MapScreenDestination(val params: MapScreenParams) : MapNavigationDestinations()
    data class MapViewScreenDestination(val params: MapViewScreenParams) : MapNavigationDestinations()
}
