package ru.lipt.domain.session.di

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.domain.session.ISessionRepository
import ru.lipt.domain.session.SessionRepository

@IgnoreKover
val sessionModules = module {
    single<ISessionRepository> {
        SessionRepository(
            koin = getKoin(),
            dataSource = get(),
        )
    }
}
