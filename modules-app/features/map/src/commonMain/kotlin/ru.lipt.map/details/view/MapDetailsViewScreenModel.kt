package ru.lipt.map.details.view

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.common.params.CreateMindMapParams
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.details.view.models.MapDetailsViewUi

@Suppress("UnusedPrivateMember")
class MapDetailsViewScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: IMindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapDetailsViewUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: SummaryViewMapResponseRemote? = null

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
            val map = mapInteractor.getMap(params.id) as SummaryViewMapResponseRemote
            _map = map
            _uiState.updateUi {
                MapDetailsViewUi(
                    title = map.title,
                    description = map.description,
                    inviteUid = map.referralId,
                    admin = map.admin.email,
                    enabledShowDeleteMap = true
                ).success()
            }
        }
    }

    fun onHideButtonClick() {
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    dialog = MapDetailsViewUi.Dialog.HideMap()
                )
            }
        }
    }

    fun onRemoveMapClick() {
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    dialog = MapDetailsViewUi.Dialog.RemoveMap()
                )
            }
        }
    }

    fun copyMapClick() {
        _uiState.navigateTo(
            NavigationTarget.CopyMap(
                CreateMindMapParams.Referral(
                    mapId = params.id,
                    title = _map?.title.orEmpty(),
                    description = _map?.description.orEmpty()
                )
            )
        )
    }

    fun cancelDeletingMap() {
        deleteMapJob?.cancel()
        hideDialog()
    }

    fun hideMapAlertConfirm() {
        deleteMapJob?.cancel()
        val map = _map ?: return

        deleteMapJob = screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi {
                    copy { ui ->
                        ui.copy(
                            dialog = MapDetailsViewUi.Dialog.HideMap(inProgress = false)
                        )
                    }
                }
            }
        ) {
            _uiState.updateUi {
                copy { ui ->
                    ui.copy(
                        dialog = MapDetailsViewUi.Dialog.HideMap(inProgress = true)
                    )
                }
            }

            mapInteractor.eraseMap(map.id, MapRemoveType.HIDE)
            _uiState.navigateTo(NavigationTarget.CatalogDestination)
        }
    }

    fun clearProgressMapAlertConfirm() {
        deleteMapJob?.cancel()
        val map = _map ?: return

        deleteMapJob = screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi {
                    copy { ui ->
                        ui.copy(
                            dialog = MapDetailsViewUi.Dialog.RemoveMap(inProgress = false)
                        )
                    }
                }
            }
        ) {
            _uiState.updateUi {
                copy { ui ->
                    ui.copy(
                        dialog = MapDetailsViewUi.Dialog.RemoveMap(inProgress = true)
                    )
                }
            }

            mapInteractor.eraseMap(map.id, MapRemoveType.DELETE)
            _uiState.navigateTo(NavigationTarget.CatalogDestination)
        }
    }

    private fun hideDialog() = _uiState.updateUi { copy { it.copy(dialog = null) } }
}
