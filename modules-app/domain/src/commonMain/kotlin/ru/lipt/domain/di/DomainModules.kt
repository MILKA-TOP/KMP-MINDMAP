package ru.lipt.domain.di

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.domain.catalog.di.catalogModules
import ru.lipt.domain.login.di.loginModules
import ru.lipt.domain.map.di.mapModules
import ru.lipt.domain.session.di.sessionModules

@IgnoreKover
val domainModules = module {
    includes(
        mapModules,
        catalogModules,
        loginModules,
        sessionModules,
    )
}
