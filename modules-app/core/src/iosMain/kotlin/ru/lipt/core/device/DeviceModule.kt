package ru.lipt.core.device

import org.koin.dsl.module

actual val deviceModule = module {
    single<ApplicationConfig> {
        IosApplicationConfig()
    }
}
