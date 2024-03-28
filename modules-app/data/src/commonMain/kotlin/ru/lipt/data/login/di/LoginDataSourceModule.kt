package ru.lipt.data.login.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.login.LoginDataSourceImpl
import ru.lipt.data.login.UnAuthedLoginDataSourceImpl
import ru.lipt.domain.login.LoginDataSource
import ru.lipt.domain.login.UnAuthedLoginDataSource

val loginDataSourceModule = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped<LoginDataSource> {
            LoginDataSourceImpl(
                config = get(),
                authedClient = get(qualifier = AUTHED_CLIENT_QUALIFIER)
            )
        }
    }
    factory<UnAuthedLoginDataSource> {
        UnAuthedLoginDataSourceImpl(
            config = get(),
            unAuthedClient = get(),
        )
    }
}
