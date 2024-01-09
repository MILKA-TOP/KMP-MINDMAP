package ru.lipt.data.login.di

import org.koin.dsl.module
import ru.lipt.data.login.LoginDataSourceImpl
import ru.lipt.domain.login.LoginDataSource

val loginDataSourceModule = module {
    single<LoginDataSource> { LoginDataSourceImpl() }
}
