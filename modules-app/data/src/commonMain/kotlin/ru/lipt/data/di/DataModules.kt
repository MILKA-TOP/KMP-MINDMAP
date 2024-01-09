package ru.lipt.data.di

import org.koin.dsl.module
import ru.lipt.data.catalog.di.catalogDataSourceModule
import ru.lipt.data.login.di.loginDataSourceModule
import ru.lipt.data.map.di.mindMapDataSourceModule
import ru.lipt.data.session.di.sessionDataSourceModule

val dataModules = module {
    includes(
        mindMapDataSourceModule,
        catalogDataSourceModule,
        loginDataSourceModule,
        sessionDataSourceModule,
    )
}
