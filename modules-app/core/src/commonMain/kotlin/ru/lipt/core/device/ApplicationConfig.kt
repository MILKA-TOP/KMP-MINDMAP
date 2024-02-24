package ru.lipt.core.device

import org.koin.core.module.Module

expect val deviceModule: Module

abstract class ApplicationConfig {
    abstract val deviceId: String

    val baseUrl: String = "http://0.0.0.0:8080"
}
