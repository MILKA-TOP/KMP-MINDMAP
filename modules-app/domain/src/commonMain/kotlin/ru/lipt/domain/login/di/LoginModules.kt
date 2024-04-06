package ru.lipt.domain.login.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.domain.login.IUnAuthedLoginInteractor
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.domain.login.UnAuthedLoginInteractor
import ru.lipt.domain.login.LoginRepository
import ru.lipt.domain.login.UnAuthedLoginRepository

@IgnoreKover
val loginModules = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped {
            LoginRepository(
                remoteDataSource = get()
            )
        }
        scoped {
            LoginInteractor(
                loginRepository = getUserSessionScope().get(),
                sessionRepository = get(),
            )
        }
    }
    factory {
        UnAuthedLoginRepository(
            remoteDataSource = get()
        )
    }
    factory<IUnAuthedLoginInteractor> {
        UnAuthedLoginInteractor(
            sessionRepository = get(),
            unAuthedLoginRepository = get()
        )
    }
}
