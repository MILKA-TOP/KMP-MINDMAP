package ru.lipt.catalog.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.catalog.common.params.CreateMindMapParams

sealed class PrivateCatalogDestinations : ScreenProvider {

    data class CreateMapDestination(val params: CreateMindMapParams) : PrivateCatalogDestinations()
    data object SearchDestination : PrivateCatalogDestinations()
}
