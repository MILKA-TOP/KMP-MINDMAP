plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
//    kotlin("native.cocoapods")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.jetbrains.kotlinx.kover")
}

private val iosBaseName = "shared"
private val androidNamespace = "ru.lipt"

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

//    cocoapods {
//        version = "1.0.0"
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        framework {
//            baseName = "shared"
//            isStatic = true
//            export(project(":modules-app:features:login"))
//        }
//        extraSpecAttributes["resource"] = "'build/cocoapods/framework/shared.framework/*.bundle'"
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Voyager.navigator)
                implementation(Dependencies.Voyager.screenModel)
                implementation(Dependencies.Voyager.transitions)
                api("dev.icerock.moko:resources:0.24.0-alpha-5")
                api("dev.icerock.moko:resources-compose:0.24.0-alpha-5") // for compose multiplatform
                // Add here you dependencies
                implementation(project(":modules-app:features:catalog"))
                implementation(project(":modules-app:features:map"))
                implementation(project(":modules-app:features:details"))
                implementation(project(":modules-app:features:testing"))
                implementation(project(":modules-app:features:login"))
                implementation(project(":modules-app:domain"))
                implementation(project(":modules-app:data"))
                implementation(project(":modules-app:core"))
                implementation(project(":modules-app:core-ui"))
            }
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
    resourcesPackage.set("ru.lipt.shared") // required
}


