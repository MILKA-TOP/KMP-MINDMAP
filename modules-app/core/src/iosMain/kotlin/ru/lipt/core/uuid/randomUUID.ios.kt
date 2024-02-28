package ru.lipt.core.uuid

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
