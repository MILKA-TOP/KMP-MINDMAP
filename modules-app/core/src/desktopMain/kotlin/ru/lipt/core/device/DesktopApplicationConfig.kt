package ru.lipt.core.device

class DesktopApplicationConfig : ApplicationConfig() {
    override val deviceId: String
        get() {
            val javaVersion = System.getProperty("java.version")
            val osName = System.getProperty("os.name")
            val osArch = System.getProperty("os.arch")
            val userName = System.getProperty("user.name")

            return "$javaVersion-$osName-$osArch-$userName".hashCode().toString()
        }
}
