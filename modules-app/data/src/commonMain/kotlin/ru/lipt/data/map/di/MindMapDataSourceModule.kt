package ru.lipt.data.map.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.map.MindMapDataSourceImpl
import ru.lipt.data.map.MindViewMapDataSourceImpl
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.MindViewMapDataSource

val mindMapDataSourceModule = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped<MindMapDataSource> {
            MindMapDataSourceImpl(
                config = get(),
                client = getUserSessionScope().get(AUTHED_CLIENT_QUALIFIER)
            )
        }
        scoped<MindViewMapDataSource> {
            MindViewMapDataSourceImpl(
                config = get(),
                client = getUserSessionScope().get(AUTHED_CLIENT_QUALIFIER)
            )
        }
    }
}
