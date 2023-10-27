package ru.lipt.shared.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import ru.lipt.map.ui.mapNavigationModule

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            mapNavigationModule,
        )
    }
