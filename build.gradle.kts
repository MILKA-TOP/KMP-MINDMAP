plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("io.gitlab.arturbosch.detekt") version ("1.19.0")
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
        classpath(Dependencies.Android.gradlePlugin)
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0")
    }
}

detekt {
    source.setFrom(
        "src/androidMain/kotlin",
        "src/commonMain/kotlin",
        "src/desktopMain/kotlin",
        "src/iosMain/kotlin"
    )
    config.setFrom("config/detekt/detekt.yml")
    buildUponDefaultConfig = false
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    exclude(
        "**/.idea/**",
        "**/build/**",
        "**/.gradle-cache/**",
        "iosApp/**",
        "**/build.gradle.kts"
    )
}

val detektAutoCorrect by tasks.registering(io.gitlab.arturbosch.detekt.Detekt::class) {
    setSource(files(projectDir))
    config.from(files("config/detekt/detekt.yml"))
    autoCorrect = true
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}
