package ru.lipt.mind

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ru.lipt.shared.di.initKoin

class MindMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MindMapApplication)
        }
    }
}
