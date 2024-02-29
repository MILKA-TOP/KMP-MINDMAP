package ru.lipt.map.details.edit

import ru.lipt.map.common.params.MapScreenParams

sealed class NavigationTarget {
    data class CopyMap(val params: Unit) : NavigationTarget()
    data class OpenUserMap(val params: MapScreenParams) : NavigationTarget()
    data object CatalogDestination : NavigationTarget()
    data object PopBack : NavigationTarget()
}
