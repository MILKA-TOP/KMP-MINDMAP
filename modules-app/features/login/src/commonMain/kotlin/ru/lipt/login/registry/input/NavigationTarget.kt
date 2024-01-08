package ru.lipt.login.registry.input

sealed class NavigationTarget {
    data object PinCreateNavigate : NavigationTarget()
}
