package ru.lipt.catalog

import org.koin.dsl.module
import ru.lipt.catalog.create.createMindMapNavigationModule
import ru.lipt.catalog.main.catalogNavigationModule
import ru.lipt.catalog.migrate.migrateMindMapNavigationModule
import ru.lipt.catalog.search.searchNavigationModule
import ru.lipt.core.kover.IgnoreKover

@IgnoreKover
val catalogModule = module {
    includes(
        catalogNavigationModule,
        createMindMapNavigationModule,
        searchNavigationModule,
        migrateMindMapNavigationModule,
    )
}
