package ru.lipt.details.editable

import ru.lipt.testing.common.params.TestEditScreenParams

sealed class NavigationTarget {
    data object SaveText : NavigationTarget()

    data class EditTest(val params: TestEditScreenParams) : NavigationTarget()
}
