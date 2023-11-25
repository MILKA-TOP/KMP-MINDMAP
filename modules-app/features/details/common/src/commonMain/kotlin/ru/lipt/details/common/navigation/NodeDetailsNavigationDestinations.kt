package ru.lipt.details.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NodeDetailsNavigationDestinations : ScreenProvider {

    data class NodeDetailsScreenDestination(val params: NodeDetailsScreenParams) :
        NodeDetailsNavigationDestinations()
}
