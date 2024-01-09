package ru.lipt.login.login

sealed class NavigationTarget {
    data object PinCreateNavigate : NavigationTarget()
}
