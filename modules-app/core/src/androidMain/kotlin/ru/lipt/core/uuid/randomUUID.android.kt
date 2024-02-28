package ru.lipt.core.uuid

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()
