package ru.lipt.details.uneditable

import ru.lipt.testing.common.params.TestCompleteScreenParams

sealed class NavigationTarget {
    data class CompleteTest(val params: TestCompleteScreenParams) : NavigationTarget()
}
