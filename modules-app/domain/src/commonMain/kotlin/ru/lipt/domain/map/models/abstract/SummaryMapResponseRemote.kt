package ru.lipt.domain.map.models.abstract

import kotlinx.serialization.Serializable

@Serializable
@Suppress("UnnecessaryAbstractClass")
abstract class SummaryMapResponseRemote {
    abstract val id: String
    abstract val title: String
}
