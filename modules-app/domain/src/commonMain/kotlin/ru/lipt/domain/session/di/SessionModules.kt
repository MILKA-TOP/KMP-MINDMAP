package ru.lipt.domain.session.di

import org.koin.dsl.module
import ru.lipt.domain.session.SessionRepository

val sessionModules = module {
    single {
        SessionRepository(
            dataSource = get(),
        )
    }
}
