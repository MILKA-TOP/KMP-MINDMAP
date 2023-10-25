plugins {
    id("ru.lipt.multiplatform")
}

multiplatformLiptLibrary {
    baseIosBinariesName = "shared"
    namespace = "ru.lipt"
}

dependencies {
    implementation(project(":modules-app:navigation"))
    implementation(project(":modules-app:features:map"))
    implementation(project(":modules-app:features:catalog"))
}
