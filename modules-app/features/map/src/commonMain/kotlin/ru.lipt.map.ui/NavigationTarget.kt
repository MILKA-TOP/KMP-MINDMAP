package ru.lipt.map.ui

import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NavigationTarget {
    data object NavigateUp : NavigationTarget()

    data class DetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()
}
