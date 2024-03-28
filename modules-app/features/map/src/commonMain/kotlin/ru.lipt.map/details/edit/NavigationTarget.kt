package ru.lipt.map.details.edit

import ru.lipt.map.common.params.MapViewScreenParams

sealed class NavigationTarget {
    data class OpenUserMap(val params: MapViewScreenParams) : NavigationTarget()
    data object CatalogDestination : NavigationTarget()
    data object PopBack : NavigationTarget()
}
