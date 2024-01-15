package ru.lipt.map.ui

import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NavigationTarget {
    data object NavigateUp : NavigationTarget()

    data class EditableDetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()
    data class UneditableDetailsScreen(
        val params: NodeDetailsScreenParams
    ) : NavigationTarget()
}
