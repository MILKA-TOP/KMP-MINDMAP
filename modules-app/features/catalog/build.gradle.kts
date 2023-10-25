plugins {
    id("ru.lipt.multiplatform")
}

multiplatformLiptLibrary {
    baseIosBinariesName = "feature.catalog"
    namespace = "ru.lipt.catalog"
}


dependencies {
    implementation(Dependencies.Voyager.navigator)
    implementation(project(":modules-app:navigation"))
}