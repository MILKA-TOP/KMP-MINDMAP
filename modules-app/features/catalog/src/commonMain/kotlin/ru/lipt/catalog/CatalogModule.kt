package ru.lipt.catalog

import org.koin.dsl.module
import ru.lipt.catalog.create.createMindMapNavigationModule
import ru.lipt.catalog.main.catalogNavigationModule

val catalogModule = module {
    includes(
        catalogNavigationModule,
        createMindMapNavigationModule,
    )
}
