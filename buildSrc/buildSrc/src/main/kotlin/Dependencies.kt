object Dependencies {

    object Kotlin {
        private const val version = "1.9.21"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:$version"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3"
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
        private const val moko_version = "0.24.0-alpha-5"

        const val mokoBase = "dev.icerock.moko:resources:$moko_version"
        const val mokoCompose = "dev.icerock.moko:resources-compose:$moko_version"
    }

    object Logs {
        private const val napierVersion = "2.7.1"

        const val napier = "io.github.aakira:napier:$napierVersion"
    }

    object Ktor {
        private const val ktorVersion = "2.3.8"

        const val core = "io.ktor:ktor-client-core:$ktorVersion"
        const val contentNegotation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
        const val okHttp = "io.ktor:ktor-client-okhttp:$ktorVersion"
        const val darwin = "io.ktor:ktor-client-darwin:$ktorVersion"
        const val android = "io.ktor:ktor-client-android:$ktorVersion"
        const val jvm = "io.ktor:ktor-client-java:$ktorVersion"
        const val auth = "io.ktor:ktor-client-auth:$ktorVersion"
    }

    object FilePicker {
        private const val pickerVersion = "3.1.0"
        const val picker = "com.darkrockstudios:mpfilepicker:$pickerVersion"
    }
    object Uri {
        private const val uriKmpVersion = "0.0.18"
        const val uriKmp = "com.eygraber:uri-kmp:$uriKmpVersion"
    }
}
