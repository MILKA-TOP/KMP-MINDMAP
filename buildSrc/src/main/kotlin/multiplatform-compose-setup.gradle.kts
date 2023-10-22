import gradle.kotlin.dsl.accessors._24fbbdb208fcbc411cffd22685b0824b.compose
import gradle.kotlin.dsl.accessors._24fbbdb208fcbc411cffd22685b0824b.kotlin
import gradle.kotlin.dsl.accessors._24fbbdb208fcbc411cffd22685b0824b.sourceSets
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()

    jvm("desktop")

    sourceSets {
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
