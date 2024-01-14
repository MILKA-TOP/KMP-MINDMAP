package ru.lipt.catalog.create

import ru.lipt.map.common.params.MapScreenParams

sealed class NavigationTarget {
    data class MindMapScreen(val params: MapScreenParams) : NavigationTarget()
}
