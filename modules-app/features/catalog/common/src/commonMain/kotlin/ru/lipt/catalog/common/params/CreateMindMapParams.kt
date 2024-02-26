package ru.lipt.catalog.common.params

sealed class CreateMindMapParams {
    data object Default : CreateMindMapParams()

    class Referral(
        val mapId: String = "",
        val title: String = "",
        val description: String = "",
    ) : CreateMindMapParams()
}
