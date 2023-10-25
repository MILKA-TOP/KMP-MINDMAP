plugins {
    id("ru.lipt.multiplatform")
}

multiplatformLiptLibrary {
    baseIosBinariesName = "base.navigation"
    namespace = "ru.lipt.navigation"
}

dependencies {
    implementation(Dependencies.Voyager.navigator)
}
