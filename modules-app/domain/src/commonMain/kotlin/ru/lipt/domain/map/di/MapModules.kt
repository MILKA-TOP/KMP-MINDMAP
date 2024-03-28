package ru.lipt.domain.map.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.MindMapLocalDataSource
import ru.lipt.domain.map.MindMapRepository
import ru.lipt.domain.map.MindMapUpdateLocalDataSource

val mapModules = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped { MindMapLocalDataSource() }
        scoped { MindMapUpdateLocalDataSource() }
        scoped {
            MindMapRepository(
                localDataSource = getUserSessionScope().get(),
                remoteDataSource = getUserSessionScope().get(),
                updateDataSource = getUserSessionScope().get()
            )
        }
        scoped {
            MindMapInteractor(
                mapRepository = getUserSessionScope().get(),
                catalogRepository = getUserSessionScope().get(),
            )
        }
    }
}
