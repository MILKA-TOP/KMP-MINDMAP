package ru.lipt.core.validate

private const val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

fun String.isEmailValid(): Boolean = this.matches(emailRegex.toRegex())
