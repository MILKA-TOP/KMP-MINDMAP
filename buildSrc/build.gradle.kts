plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


gradlePlugin {
    plugins.register("ru.lipt.multiplatform") {
        id = "ru.lipt.multiplatform"
        implementationClass = "ru.lipt.build.MultiplatformModulePlugin"
    }
}

dependencies {
    implementation(Dependencies.Kotlin.gradlePlugin)
    implementation(Dependencies.Compose.gradlePlugin)
    implementation(Dependencies.Android.gradlePlugin)
    implementation(Dependencies.Android.gradleApiPlugin)
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin/")
}