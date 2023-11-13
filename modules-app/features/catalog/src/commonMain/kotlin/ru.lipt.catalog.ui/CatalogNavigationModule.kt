package ru.lipt.catalog.ui

import org.koin.dsl.module

val catalogNavigationModule = module {
    factory {
        CatalogScreenModel(
            catalogInteractor = get()
        )
    }
}
