plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

private val iosBaseName = "modules.data"
private val androidNamespace = "ru.lipt.data"

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
                implementation(Dependencies.Setings.multiplatformSettings)
                implementation(Dependencies.Setings.multiplatformSettingsNoArgs)
                implementation(Dependencies.Ktor.core)
                implementation(Dependencies.Ktor.contentNegotation)
                implementation(Dependencies.Ktor.serialization)
                implementation(Dependencies.Ktor.auth)
                implementation(Dependencies.Kotlin.serialization)
                // Add here you dependencies
                implementation(project(":modules-app:navigation"))
                implementation(project(":modules-app:domain"))
                implementation(project(":modules-app:core"))
                implementation(project(":modules-app:core-ui"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.android)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Dependencies.Ktor.darwin)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val desktopMain by getting {
            dependencies {
                implementation(Dependencies.Ktor.jvm)
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
