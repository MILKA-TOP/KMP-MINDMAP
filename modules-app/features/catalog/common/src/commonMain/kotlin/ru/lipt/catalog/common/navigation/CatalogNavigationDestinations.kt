package ru.lipt.catalog.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.catalog.common.params.CreateMindMapParams

sealed class CatalogNavigationDestinations : ScreenProvider {
    data class CreateMapDestination(val params: CreateMindMapParams) : CatalogNavigationDestinations()
    data object CatalogScreenDestination : CatalogNavigationDestinations()
    data object MigrateScreenDestination : CatalogNavigationDestinations()
}
