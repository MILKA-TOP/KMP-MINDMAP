plugins {
    kotlin("multiplatform")
    alias(libs.plugins.ksp)
    id("com.android.library")
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.jetbrains.kotlinx.kover")
    alias(libs.plugins.mockmp)
}

private val iosBaseName = "feature.testing"
private val androidNamespace = "ru.lipt.testing"

kotlin {
    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export("dev.icerock.moko:resources:0.24.0-alpha-5")
            baseName = iosBaseName
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Voyager.koin)
                implementation(Dependencies.Voyager.navigator)
                implementation(Dependencies.Voyager.screenModel)
                implementation(project(":modules-app:core"))
                implementation(project(":modules-app:core-ui"))
                api(Dependencies.Resources.mokoBase)
                api(Dependencies.Resources.mokoCompose)
                // Add here you dependencies
                implementation(project(":modules-app:features:testing:common"))
                implementation(project(":modules-app:navigation"))
                implementation(project(":modules-app:domain"))
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(libs.bundles.commonTest)

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0") // Check for the latest version
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:1.8.0") // Check for the latest version
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
                implementation(compose.desktop.common)
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
multiplatformResources {
    resourcesPackage.set(androidNamespace) // required
}
mockmp {
    usesHelper = true
    installWorkaround()
}
