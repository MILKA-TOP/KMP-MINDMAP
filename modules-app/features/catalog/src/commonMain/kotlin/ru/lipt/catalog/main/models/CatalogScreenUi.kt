package ru.lipt.catalog.main.models

import androidx.compose.runtime.Immutable
import ru.lipt.catalog.models.MapCatalogElement
import ru.lipt.domain.catalog.models.CatalogMindMap

@Immutable
data class CatalogScreenUi(
    val maps: List<MapCatalogElement> = listOf(),
    val isLoadingInProgress: Boolean = false,
) {
    companion object {
        fun List<CatalogMindMap>.toUi() = this.map { map ->
            MapCatalogElement(
                id = map.id,
                title = map.title,
                description = map.description,
                isEnabledEdit = map.isEnableEdit,
                type = if (map.isEnableEdit) MapCatalogElement.MapType.EDITABLE else MapCatalogElement.MapType.VIEW,
                adminEmail = map.admin.email,
                isSaved = map.isSaved,
                isPrivate = map.isPrivate,
            )
        }
    }
}
