package ru.lipt.data.core.network.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.core.network.NetworkModule
import ru.lipt.data.core.network.NetworkModule.provideJson
import ru.lipt.data.core.network.NetworkRequestExceptionsHandler

@IgnoreKover
val networkModule = module {
    single<Json> {
        provideJson()
    }
    single<HttpClient> {
        NetworkModule.provideBaseHttpClient(
            json = get(),
            exceptionsHandler = NetworkRequestExceptionsHandler(
                sessionRepository = get(),
            ),
            deviceConfig = get()
        )
    }
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped<HttpClient>(AUTHED_CLIENT_QUALIFIER) {
            NetworkModule.provideAuthedHttpClient(
                json = get(),
                exceptionsHandler = NetworkRequestExceptionsHandler(
                    sessionRepository = get(),
                ),
                deviceConfig = get(),
                sessionRepository = get(),
            )
        } }
}
