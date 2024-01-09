package ru.lipt.catalog.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class PrivateCatalogDestinations : ScreenProvider {

    data object CreateMapDestination : PrivateCatalogDestinations()
}
