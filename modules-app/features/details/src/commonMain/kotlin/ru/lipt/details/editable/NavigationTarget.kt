package ru.lipt.details.editable

import ru.lipt.details.common.params.NodeDetailsScreenParams

sealed class NavigationTarget {
    data object SaveText : NavigationTarget()

    data class EditTest(val params: NodeDetailsScreenParams) : NavigationTarget()
}
