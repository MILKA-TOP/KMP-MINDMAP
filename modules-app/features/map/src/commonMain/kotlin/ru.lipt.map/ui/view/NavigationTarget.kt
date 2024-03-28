package ru.lipt.map.ui.view

import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NavigationTarget {
    data object NavigateUp : NavigationTarget()

    data class UneditableDetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()
}
