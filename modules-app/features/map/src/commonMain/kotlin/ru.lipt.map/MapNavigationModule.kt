package ru.lipt.map

import org.koin.dsl.module
import ru.lipt.map.details.mapDetailsModule
import ru.lipt.map.ui.mapScreenModule

val mapNavigationModule = module {
    includes(
        mapScreenModule,
        mapDetailsModule,
    )
}
