plugins {
    kotlin("multiplatform")
    alias(libs.plugins.ksp)
    id("com.android.library")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

private val iosBaseName = "base.domain"
private val androidNamespace = "ru.lipt.domain"

kotlin {
    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = iosBaseName
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Voyager.koin)
                implementation(Dependencies.Voyager.navigator)
                implementation(Dependencies.Voyager.screenModel)
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Kotlin.serialization)

                implementation(project(":modules-app:core"))
                implementation(project(":modules-app:core-ui"))
                // Add here you dependencies
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val desktopMain by getting {
            dependencies {
                dependsOn(commonMain)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = androidNamespace

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
