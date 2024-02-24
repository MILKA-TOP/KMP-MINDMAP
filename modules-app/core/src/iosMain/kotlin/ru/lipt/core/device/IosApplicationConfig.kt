package ru.lipt.core.device

import platform.UIKit.UIDevice

class IosApplicationConfig : ApplicationConfig() {
    override val deviceId: String
        get() = UIDevice.currentDevice.identifierForVendor()?.UUIDString.orEmpty()
}
