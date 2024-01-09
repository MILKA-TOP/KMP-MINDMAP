package ru.lipt.shared.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import ru.lipt.catalog.main.catalogNavigationModule
import ru.lipt.data.di.dataModules
import ru.lipt.details.detailsNavigationModule
import ru.lipt.domain.di.domainModules
import ru.lipt.map.ui.mapNavigationModule
import ru.lipt.testing.testingNavigationModule
import ru.lipt.login.loginNavigationModule

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            mapNavigationModule,
            catalogNavigationModule,
            detailsNavigationModule,
            loginNavigationModule,
            testingNavigationModule,
            domainModules,
            dataModules,
        )
    }
