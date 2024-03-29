package ru.lipt.map

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.map.details.mapDetailsModule
import ru.lipt.map.ui.mapScreenModule
import ru.lipt.map.ui.view.mapViewScreenModule

@IgnoreKover
val mapNavigationModule = module {
    includes(
        mapScreenModule,
        mapDetailsModule,
        mapViewScreenModule,
    )
}
