object Dependencies {

    object Kotlin {
        private const val version = "1.9.10"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Compose {
        private const val version = "1.5.3"
        const val gradlePlugin = "org.jetbrains.compose:compose-gradle-plugin:$version"
    }

    object Android {
        const val gradlePlugin = "com.android.tools.build:gradle:8.1.0"
        const val composeActivity = "androidx.activity:activity-compose:1.5.1"
    }

}