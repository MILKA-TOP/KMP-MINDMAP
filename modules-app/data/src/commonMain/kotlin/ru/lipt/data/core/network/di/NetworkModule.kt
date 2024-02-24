package ru.lipt.data.core.network.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ru.lipt.data.core.network.NetworkModule
import ru.lipt.data.core.network.NetworkModule.provideJson
import ru.lipt.data.core.network.NetworkRequestExceptionsHandler

val networkModule = module {
    single<Json> {
        provideJson()
    }
    single<HttpClient> {
        NetworkModule.provideAuthorizedHttpClient(
            json = get(),
            exceptionsHandler = NetworkRequestExceptionsHandler(
                sessionRepository = get(),
            ),
            deviceConfig = get()
        )
    }
}
