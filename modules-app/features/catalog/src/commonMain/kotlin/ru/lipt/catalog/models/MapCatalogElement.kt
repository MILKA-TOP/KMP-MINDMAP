package ru.lipt.catalog.models

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.StringResource
import ru.lipt.catalog.MR
import ru.lipt.core.kover.IgnoreKover

@Immutable
data class MapCatalogElement(
    val id: String,
    val title: String,
    val adminEmail: String,
    val description: String,
    val type: MapType,
    val isPrivate: Boolean,
    val isEnabledEdit: Boolean = false,
    val isSaved: Boolean = false,
) {
    val showFirstTypeActionLine: Boolean = isEnabledEdit || isSaved
    val showPrivateIcon: Boolean = !isSaved && isPrivate

    @IgnoreKover
    enum class MapType(val stringRes: StringResource) {
        EDITABLE(MR.strings.catalog_map_type_editable), VIEW(MR.strings.catalog_map_type_interactable)
    }
}
