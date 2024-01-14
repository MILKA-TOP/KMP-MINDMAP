package ru.lipt.catalog.search

import ru.lipt.map.common.params.MapScreenParams

sealed class NavigationTarget {
    data class ToMapNavigate(val params: MapScreenParams) : NavigationTarget()
}
