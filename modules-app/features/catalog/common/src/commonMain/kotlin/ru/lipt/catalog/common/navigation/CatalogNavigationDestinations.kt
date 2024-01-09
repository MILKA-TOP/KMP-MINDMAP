package ru.lipt.catalog.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class CatalogNavigationDestinations : ScreenProvider {

    data object CatalogScreenDestination : CatalogNavigationDestinations()
}
