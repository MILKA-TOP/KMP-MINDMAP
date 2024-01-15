package ru.lipt.details.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NodeDetailsNavigationDestinations : ScreenProvider {

    data class EditableNodeDetailsScreenDestination(val params: NodeDetailsScreenParams) :
        NodeDetailsNavigationDestinations()
    data class UneditableNodeDetailsScreenDestination(val params: NodeDetailsScreenParams) :
        NodeDetailsNavigationDestinations()
}
