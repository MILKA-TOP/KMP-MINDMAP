package ru.lipt.build

open class MultiplatformModuleExtensions {
    var baseIosBinariesName: String? = null
    var namespace: String? = null
    val compileSdk: Int = 34
    val minSdk: Int = 24

    companion object {
        const val NAME = "multiplatformLiptLibrary"
    }
}