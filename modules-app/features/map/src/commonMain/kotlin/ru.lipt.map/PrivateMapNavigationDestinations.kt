package ru.lipt.map

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.map.common.params.MapScreenParams

sealed class PrivateMapNavigationDestinations : ScreenProvider {

    data class MapDetails(val params: MapScreenParams) : PrivateMapNavigationDestinations()
}
