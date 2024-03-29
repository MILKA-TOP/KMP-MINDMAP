package ru.lipt.data.session.di

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.data.session.SessionDataSourceImpl
import ru.lipt.domain.session.SessionDataSource

@IgnoreKover
val sessionDataSourceModule = module {
    single<SessionDataSource> { SessionDataSourceImpl() }
}
