package ru.lipt.catalog.main

import ru.lipt.map.common.params.MapScreenParams

sealed class NavigationTarget {
    data class MapDestination(val params: MapScreenParams) : NavigationTarget()
    data object HelloScreenDestination : NavigationTarget()
}
