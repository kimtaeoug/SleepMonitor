package ted.gun0912.sleep.ui.feature.sleep.dialog

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ted.gun0912.sleep.domain.usecase.SaveShowChargePhoneNoticeUseCase
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.feature.sleep.dialog.ChargePhoneNoticeViewModel.Event
import javax.inject.Inject

@HiltViewModel
class ChargePhoneNoticeViewModel @Inject constructor(
    private val saveShowChargePhoneNoticeUseCase: SaveShowChargePhoneNoticeUseCase,
) : BaseViewModel<Event>() {

    val isChecked = MutableStateFlow(false)

    fun startSleep() {
        launch { saveShowChargePhoneNoticeUseCase(!isChecked.value).await() }
        event(Event.StartSleep)
    }

    sealed interface Event : ViewEvent {
        object StartSleep : Event
    }
}
