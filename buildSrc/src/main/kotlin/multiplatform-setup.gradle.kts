import gradle.kotlin.dsl.accessors._4eeeb394abedb86e44a0235bb97e3616.kotlin
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("kapt")
}

kotlin {
    jvm("desktop")
    androidTarget()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}