package ru.lipt.details.uneditable

import ru.lipt.testing.common.params.TestCompleteScreenParams
import ru.lipt.testing.common.params.TestingResultParams

sealed class NavigationTarget {
    data class CompleteTest(val params: TestCompleteScreenParams) : NavigationTarget()
    data class TestResult(val params: TestingResultParams) : NavigationTarget()
}
