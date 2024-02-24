package ru.lipt.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class AndroidApplicationConfig(
    context: Context
) : ApplicationConfig() {
    @SuppressLint("HardwareIds")
    override val deviceId: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
