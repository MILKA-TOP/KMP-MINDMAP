package ru.lipt.domain.map.models.abstract

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

val summaryMapJsonModule = SerializersModule {
    polymorphic(SummaryMapResponseRemote::class) {
        subclass(SummaryEditMapResponseRemote::class)
        subclass(SummaryViewMapResponseRemote::class)
    }
}
