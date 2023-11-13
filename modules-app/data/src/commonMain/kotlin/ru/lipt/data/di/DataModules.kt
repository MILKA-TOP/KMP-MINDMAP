package ru.lipt.data.di

import org.koin.dsl.module
import ru.lipt.data.catalog.di.catalogDataSourceModule
import ru.lipt.data.map.di.mindMapDataSourceModule

val dataModules = module {
    includes(
        mindMapDataSourceModule,
        catalogDataSourceModule
    )
}
