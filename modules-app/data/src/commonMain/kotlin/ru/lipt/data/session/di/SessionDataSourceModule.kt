package ru.lipt.data.session.di

import org.koin.dsl.module
import ru.lipt.data.session.SessionDataSourceImpl
import ru.lipt.domain.session.SessionDataSource

val sessionDataSourceModule = module {
    single<SessionDataSource> { SessionDataSourceImpl() }
}
