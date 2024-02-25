package ru.lipt.data.login.di

import org.koin.dsl.module
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.login.LoginDataSourceImpl
import ru.lipt.domain.login.LoginDataSource

val loginDataSourceModule = module {
    single<LoginDataSource> {
        LoginDataSourceImpl(
            config = get(),
            unAuthedClient = get(),
            authedClient = get(qualifier = AUTHED_CLIENT_QUALIFIER)
        )
    }
}
