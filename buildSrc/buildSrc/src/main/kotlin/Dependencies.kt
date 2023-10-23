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

    object Voyager {
        private const val version = "1.0.0-rc05"

        const val navigator = "cafe.adriel.voyager:voyager-navigator:$version"
        const val bottomSheetNavigator = "cafe.adriel.voyager:voyager-bottom-sheet-navigator:$version"
        const val tabNavigator = "cafe.adriel.voyager:voyager-tab-navigator:$version"
        const val transitions = "cafe.adriel.voyager:voyager-transitions:$version"
    }

    object Koin {
        private const val koin_version = "3.5.0"

        const val core = "io.insert-koin:koin-core:$koin_version"
        const val android = "io.insert-koin:koin-android:$koin_version"
    }

}