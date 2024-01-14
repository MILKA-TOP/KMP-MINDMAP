package ru.lipt.login.pin.input

sealed class NavigationTarget {
    data object HelloScreenNavigate : NavigationTarget()
    data object CatalogScreenNavigate : NavigationTarget()
}
