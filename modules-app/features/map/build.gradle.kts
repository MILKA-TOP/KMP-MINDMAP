plugins {
    id("ru.lipt.multiplatform")
}

multiplatformLiptLibrary {
    baseIosBinariesName = "feature.map"
    namespace = "ru.lipt.map"
}

dependencies {
    implementation(Dependencies.Voyager.navigator)
    implementation(project(":modules-app:navigation"))
}