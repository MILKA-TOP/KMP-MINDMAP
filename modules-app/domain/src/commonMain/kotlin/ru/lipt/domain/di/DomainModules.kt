package ru.lipt.domain.di

import org.koin.dsl.module
import ru.lipt.domain.catalog.di.catalogModules
import ru.lipt.domain.map.di.mapModules

val domainModules = module {
    includes(
        mapModules,
        catalogModules,
    )
}
