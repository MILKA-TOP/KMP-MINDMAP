package ru.lipt.catalog.main.models

import androidx.compose.runtime.Immutable
import ru.lipt.catalog.models.MapCatalogElement
import ru.lipt.domain.catalog.models.CatalogMindMap

@Immutable
data class CatalogScreenUi(
    val maps: List<MapCatalogElement> = listOf()
) {
    companion object {
        fun List<CatalogMindMap>.toUi() = this.map { map ->
            MapCatalogElement(
                id = map.id,
                title = map.title,
                description = map.description,
            )
        }
    }
}
