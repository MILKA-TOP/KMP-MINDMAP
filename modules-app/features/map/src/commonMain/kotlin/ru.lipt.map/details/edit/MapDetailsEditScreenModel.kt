package ru.lipt.map.details.edit

// import ru.lipt.domain.map.models.MapType
// import ru.lipt.domain.map.models.MindMap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.details.edit.models.MapDetailsEditUi
import ru.lipt.map.details.edit.models.UserUi

@Suppress("UnusedPrivateMember")
class MapDetailsEditScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapDetailsEditUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: SummaryEditMapResponseRemote? = null
    private var _initTitle: String = ""
    private var _currentTitle: String = ""
    private var _initDescription: String = ""
    private var _currentDescription: String = ""

    private var deleteMapJob: Job? = null

    init {
        init()
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun init() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { Unit.error() }
            }
        ) {
            _uiState.updateUi { loading() }
            val map = mapInteractor.getMap(params.id) as SummaryEditMapResponseRemote
            _map = map
            _initTitle = map.title
            _currentTitle = _initTitle
            _initDescription = map.description
            _currentDescription = _initDescription
            _uiState.updateUi {
                MapDetailsEditUi(
                    title = map.title,
                    description = map.description,
                    inviteUid = map.referralId,
                    admin = map.admin.email,
                    users = map.accessUsers.map {
                        UserUi(id = it.id, email = it.email)
                    },
                    enabledShowDeleteMap = true
                ).success()
            }
        }
    }

    fun hideDialog() = _uiState.updateUi { copy { it.copy(dialog = null) } }

    fun onUserClick(userId: String) {
        val user = _map?.accessUsers?.first { it.id == userId } ?: return
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    dialog = MapDetailsEditUi.Dialog.UserMap(userId, user.email)
                )
            }
        }
    }

    fun onDeleteButtonClick() {
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    dialog = MapDetailsEditUi.Dialog.DeleteMap()
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
        deleteMapJob?.cancel()
        val map = _map ?: return

        deleteMapJob = screenModelScope.launchCatching(
            catchBlock = {
            },
            finalBlock = {
                _uiState.updateUi {
                    copy { ui ->
                        ui.copy(
                            dialog = MapDetailsEditUi.Dialog.DeleteMap(inProgress = false)
                        )
                    }
                }
            }
        ) {
            _uiState.updateUi {
                copy { ui ->
                    ui.copy(
                        dialog = MapDetailsEditUi.Dialog.DeleteMap(inProgress = true)
                    )
                }
            }

            mapInteractor.deleteMap(map.id)
            _uiState.navigateTo(NavigationTarget.CatalogDestination)
        }
    }

    fun onTitleTextChanged(s: String) {
        _currentTitle = s
        _uiState.updateUi { copy { it.copy(title = s).setButtonEnabled() } }
    }

    fun onDescriptionTextChanged(s: String) {
        _currentDescription = s
        _uiState.updateUi { copy { it.copy(description = s).setButtonEnabled() } }
    }

    fun onSaveClick() {
        screenModelScope.launchCatching {
            mapInteractor.saveTitleAndData(params.id, _currentTitle, _currentDescription)

            _uiState.navigateTo(NavigationTarget.PopBack)
        }
    }

    private fun MapDetailsEditUi.setButtonEnabled() = copy(
        buttonIsEnabled = _initTitle != title || _initDescription != description
    )
}
