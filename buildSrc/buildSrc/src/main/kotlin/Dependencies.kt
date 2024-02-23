object Dependencies {

    object Kotlin {
        private const val version = "1.9.20"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Compose {
        private const val version = "1.6.0-beta02"
        const val gradlePlugin = "org.jetbrains.compose:compose-gradle-plugin:$version"
    }

    object Android {
        const val gradlePlugin = "com.android.tools.build:gradle:8.1.0"
        const val gradleApiPlugin = "com.android.tools.build:gradle-api:8.1.0"
        const val composeActivity = "androidx.activity:activity-compose:1.5.1"
    }

    object Voyager {
        private const val version = "1.0.0"

        const val navigator = "cafe.adriel.voyager:voyager-navigator:$version"
        const val screenModel = "cafe.adriel.voyager:voyager-screenmodel:$version"
        const val bottomSheetNavigator = "cafe.adriel.voyager:voyager-bottom-sheet-navigator:$version"
        const val tabNavigator = "cafe.adriel.voyager:voyager-tab-navigator:$version"
        const val transitions = "cafe.adriel.voyager:voyager-transitions:$version"
        const val koin = "cafe.adriel.voyager:voyager-koin:$version"
    }

    object Koin {
        private const val koin_version = "3.5.0"

        const val core = "io.insert-koin:koin-core:$koin_version"
        const val android = "io.insert-koin:koin-android:$koin_version"
        const val compose = "io.insert-koin:koin-compose:$koin_version"
    }

    object Setings {
        private const val settings_version = "1.1.1"

        const val multiplatformSettings = "com.russhwolf:multiplatform-settings:$settings_version"
        const val multiplatformSettingsNoArgs = "com.russhwolf:multiplatform-settings-no-arg:$settings_version"
    }

    object Resources {
        private const val moko_version = "0.23.0"

        const val mokoBase = "dev.icerock.moko:resources:$moko_version"
        const val mokoCompose = "dev.icerock.moko:resources-compose:$moko_version"
    }

    object Logs {
        private const val napierVersion = "2.7.1"

        const val napier = "io.github.aakira:napier:$napierVersion"
    }
}
