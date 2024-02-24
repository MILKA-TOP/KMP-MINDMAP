package ru.lipt.core.di

import org.koin.dsl.module
import ru.lipt.core.device.deviceModule

val coreModule = module {
    includes(
        deviceModule,
    )
}
