package ru.lipt.catalog.search.models

import androidx.compose.runtime.Immutable
import ru.lipt.core.LoadingState
import ru.lipt.core.idle

@Immutable
data class SearchScreenUi(
    val searchText: String = "",
    val searchFieldEnabled: Boolean = true,
    val enterPasswordAlert: EnterAlertModel? = null,
    val content: LoadingState<SearchContentUi, Unit> = idle(),
)

@Immutable
data class SearchContentUi(
    val maps: List<MapQueryElement> = listOf()
)
