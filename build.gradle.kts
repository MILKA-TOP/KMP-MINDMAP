plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    kotlin("plugin.serialization") version "1.9.22"
    id("io.gitlab.arturbosch.detekt") version ("1.19.0")
    kotlin("native.cocoapods")
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
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
        classpath("dev.icerock.moko:resources-generator:0.24.0-alpha-5")
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
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

koverReport {
    filters {
        excludes {
            packages(
                "*hilt_aggregated_deps*",
                "*codegen*",
                "*resources*",

                // App Specific
                "org.jdc.template.ui",
            )

            classes(
                "*Fragment",
                "*Fragment\$*",
                "*Activity",
                "*Activity\$*",
                "*.databinding.*",
                "*.BuildConfig",
                "*Factory",
                    "*_HiltModules*",
                "*_Impl*",
                "*Screen",
                "*Screen\$Content*",
                "*ComposableSingletons*",
                "*NavigationTarget*",
                "*Hilt*",
                "*Initializer*",

                // App Specific
                "*MainAppScaffoldWithNavBarKt*",
                "*Destinations*",
                "*MR*",
                "*ModuleKt*",
                "*ModulesKt*",
            )

            annotatedBy(
                "*Composable*",
                "*HiltAndroidApp*",
                "*HiltViewModel*",
                "*HiltWorker*",
                "*AndroidEntryPoint*",
                "*Immutable*",
                "*Module*",
                "*SuppressCoverage*",
                "*IgnoreKover*",
            )
        }
    }
}
dependencies {
    kover(project(":shared"))
    kover(project(":modules-app:features:catalog"))
    kover(project(":modules-app:features:catalog"))
    kover(project(":modules-app:features:map"))
    kover(project(":modules-app:features:details"))
    kover(project(":modules-app:features:testing"))
    kover(project(":modules-app:features:login"))
    kover(project(":modules-app:domain"))
    kover(project(":modules-app:data"))
}