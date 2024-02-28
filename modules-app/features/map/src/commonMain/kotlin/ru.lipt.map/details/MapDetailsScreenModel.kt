package ru.lipt.map.details

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.idle
import ru.lipt.domain.map.MindMapInteractor
// import ru.lipt.domain.map.models.MapType
// import ru.lipt.domain.map.models.MindMap
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.details.models.MapDetailsUi

@Suppress("UnusedPrivateMember")
class MapDetailsScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapDetailsUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

//    private var _map: MindMap? = null
//    private var _mapType: MapType = MapType.VIEW

    private var deleteMapJob: Job? = null

    init {
        init()
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun init() {
//        screenModelScope.launchCatching(
//            catchBlock = {
//                _uiState.updateUi { Unit.error() }
//            }
//        ) {
//            _uiState.updateUi { loading() }
//            delay(2_000L)
//            val map = mapInteractor.getMap(params.id)
//            _map = map
//            _mapType = map.viewType
//            _uiState.updateUi {
//                MapDetailsUi(
//                    title = map.title,
//                    description = map.description,
//                    inviteUid = map.id,
//                    admin = map.admin.email,
//                    users = map.users.map {
//                        UserUi(id = it.id, email = it.email)
//                    },
// //                    enabledShowDeleteMap = map.viewType == MapType.EDIT
//                    enabledShowDeleteMap = true
//                ).success()
//            }
//        }
    }

    fun hideDialog() = _uiState.updateUi { copy { it.copy(dialog = null) } }

    fun onUserClick(userId: String) {
//        val user = _map?.users?.first { it.id == userId } ?: return
//        _uiState.updateUi {
//            copy { ui ->
//                ui.copy(
//                    dialog = MapDetailsUi.Dialog.UserMap(userId, user.email)
//                )
//            }
//        }
    }

    fun onDeleteButtonClick() {
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    dialog = MapDetailsUi.Dialog.DeleteMap()
                )
            }
        }
    }

    fun copyMapClick() {
        _uiState.navigateTo(NavigationTarget.CopyMap(Unit))
    }

    @Suppress("UnusedPrivateMember")
    fun onUserAlertConfirm(userId: String) {
        _uiState.navigateTo(
            NavigationTarget.OpenUserMap(
                params = MapScreenParams(params.id)
            )
        )
    }

    fun cancelDeletingMap() {
        deleteMapJob?.cancel()
        hideDialog()
    }

    fun deleteMapAlertConfirm() {
//        deleteMapJob?.cancel()
//
//        val map = _map ?: return
//
//        deleteMapJob = screenModelScope.launchCatching(
//            catchBlock = {
//            },
//            finalBlock = {
//                _uiState.updateUi {
//                    copy { ui ->
//                        ui.copy(
//                            dialog = MapDetailsUi.Dialog.DeleteMap(inProgress = false)
//                        )
//                    }
//                }
//            }
//        ) {
//            _uiState.updateUi {
//                copy { ui ->
//                    ui.copy(
//                        dialog = MapDetailsUi.Dialog.DeleteMap(inProgress = true)
//                    )
//                }
//            }
//
//            delay(2_000L)
//            mapInteractor.deleteMap(map.id)
//            _uiState.navigateTo(NavigationTarget.CatalogDestination)
//        }
    }
}
