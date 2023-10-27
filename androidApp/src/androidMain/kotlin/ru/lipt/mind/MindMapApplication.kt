package ru.lipt.mind

import android.app.Application
import ru.lipt.shared.di.initKoin

class MindMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
        }
    }
}
