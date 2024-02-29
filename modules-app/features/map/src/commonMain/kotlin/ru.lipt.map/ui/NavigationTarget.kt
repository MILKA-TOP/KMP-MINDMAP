package ru.lipt.map.ui

import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.map.common.params.MapScreenParams

sealed class NavigationTarget {
    data object NavigateUp : NavigationTarget()

    data class EditableDetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()

    data class MapDetailsEditScreenDestination(
        val params: MapScreenParams
    ) : NavigationTarget()
    data class MapDetailsViewScreenDestination(
        val params: MapScreenParams
    ) : NavigationTarget()

    data class UneditableDetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()
}
