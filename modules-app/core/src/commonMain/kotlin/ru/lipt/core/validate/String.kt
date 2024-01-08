package ru.lipt.core.validate

private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

fun String.isEmailValid(): Boolean = this.matches(EMAIL_REGEX.toRegex())
