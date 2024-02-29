package ru.lipt.map.details.view

import ru.lipt.catalog.common.params.CreateMindMapParams

sealed class NavigationTarget {
    data class CopyMap(val params: CreateMindMapParams) : NavigationTarget()
    data object CatalogDestination : NavigationTarget()
    data object PopBack : NavigationTarget()
}
