package ru.lipt.domain.login.di

import org.koin.dsl.module
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.domain.login.LoginLocalDataSource
import ru.lipt.domain.login.LoginRepository

val loginModules = module {
    single { LoginLocalDataSource() }
    single {
        LoginRepository(
            localDataSource = get(),
            remoteDataSource = get()
        )
    }
    single {
        LoginInteractor(
            loginRepository = get(),
            sessionRepository = get(),
        )
    }
}
