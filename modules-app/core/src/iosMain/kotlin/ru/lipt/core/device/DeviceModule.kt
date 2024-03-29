package ru.lipt.core.device

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover

@IgnoreKover
actual val deviceModule = module {
    single<ApplicationConfig> {
        IosApplicationConfig()
    }
}
