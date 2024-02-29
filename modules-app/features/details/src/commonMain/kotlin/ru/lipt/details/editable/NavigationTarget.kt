package ru.lipt.details.editable

import ru.lipt.testing.common.params.TestEditScreenParams

sealed class NavigationTarget {
    data class EditTest(val params: TestEditScreenParams) : NavigationTarget()
    data object SuccessSave : NavigationTarget()
    data object SuccessRemove : NavigationTarget()
}
