package ru.lipt.catalog.search.models

import androidx.compose.runtime.Immutable
import ru.lipt.catalog.models.MapCatalogElement
import ru.lipt.core.LoadingState
import ru.lipt.core.idle

@Immutable
data class SearchScreenUi(
    val searchText: String = "",
    val searchFieldEnabled: Boolean = true,
    val addMindMapAlert: SearchAlerts? = null,
    val content: LoadingState<SearchContentUi, Unit> = idle(),
)

@Immutable
sealed class SearchAlerts {
    open val inProgress: Boolean = false
    open val isConfirmButtonEnabled: Boolean = false

    data class PublicMap(val title: String, val isInProgress: Boolean = false) : SearchAlerts() {
        override val inProgress: Boolean = isInProgress
        override val isConfirmButtonEnabled = true
    }

    data class PrivateMap(val title: String, val password: String = "", override val inProgress: Boolean = false) : SearchAlerts() {
        override val isConfirmButtonEnabled = password.isNotEmpty()
    }

    fun copy(inProgressUpdated: Boolean): SearchAlerts = when (val alert = this) {
        is PublicMap -> alert.copy(isInProgress = inProgressUpdated)
        is PrivateMap -> alert.copy(inProgress = inProgressUpdated)
    }
}

@Immutable
data class SearchContentUi(
    val maps: List<MapCatalogElement> = listOf()
)
