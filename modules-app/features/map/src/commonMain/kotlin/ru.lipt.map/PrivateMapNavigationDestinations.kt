package ru.lipt.map

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.map.common.params.MapScreenParams

sealed class PrivateMapNavigationDestinations : ScreenProvider {

    data class MapEditDetails(val params: MapScreenParams) : PrivateMapNavigationDestinations()
    data class MapViewDetails(val params: MapScreenParams) : PrivateMapNavigationDestinations()
}
