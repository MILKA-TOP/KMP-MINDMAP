package ru.lipt.build

import Dependencies
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class MultiplatformModulePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("kotlin-multiplatform")
        project.plugins.apply("com.android.library")
        project.plugins.apply("org.jetbrains.compose")

        val extension =
            project.extensions.create<MultiplatformModuleExtensions>(MultiplatformModuleExtensions.NAME)


        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
            androidTarget()
            jvm("desktop")
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget: KotlinNativeTarget ->
                iosTarget.binaries.framework {
                    baseName = extension.baseIosBinariesName ?: "shared"
                    isStatic = true
                }
            }

            val composeVersion = "1.5.3"
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
                implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
                implementation("org.jetbrains.compose.material:material:$composeVersion")
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation("org.jetbrains.compose.components:components-resources:$composeVersion")
                implementation(Dependencies.Koin.core)
            }
            sourceSets.maybeCreate("androidMain").dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
            }
            sourceSets.maybeCreate("iosX64Main")
            sourceSets.maybeCreate("iosArm64Main")
            sourceSets.maybeCreate("iosSimulatorArm64Main")
            sourceSets.maybeCreate("iosMain").apply {
                dependsOn(sourceSets.getByName("commonMain"))
                sourceSets.getByName("iosX64Main").dependsOn(this)
                sourceSets.getByName("iosArm64Main").dependsOn(this)
                sourceSets.getByName("iosSimulatorArm64Main").dependsOn(this)
            }
            sourceSets.maybeCreate("desktopMain").apply {
                dependsOn(sourceSets.getByName("commonMain"))
                dependencies {
                    implementation("org.jetbrains.compose.desktop:desktop:$composeVersion")
                }
            }
        }

        project.extensions.configure(LibraryExtension::class.java) {
            compileSdk = extension.compileSdk
            namespace = extension.namespace ?: "ru.lipt"

            sourceSets.maybeCreate("main").apply {
                manifest.srcFile("src/androidMain/AndroidManifest.xml")
                res.srcDirs("src/androidMain/res")
                resources.srcDirs("src/commonMain/resources")
            }

            defaultConfig {
                minSdk = extension.minSdk
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
//            kotlin {
//                jvmToolchain(17)
//            }
        }
    }
}