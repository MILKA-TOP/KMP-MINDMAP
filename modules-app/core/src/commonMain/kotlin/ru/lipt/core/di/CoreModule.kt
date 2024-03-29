package ru.lipt.core.di

import org.koin.dsl.module
import ru.lipt.core.device.deviceModule
import ru.lipt.core.kover.IgnoreKover

@IgnoreKover
val coreModule = module {
    includes(
        deviceModule,
    )
}
