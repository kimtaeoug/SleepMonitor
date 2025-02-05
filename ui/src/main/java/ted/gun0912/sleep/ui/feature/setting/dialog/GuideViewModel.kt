package ted.gun0912.sleep.ui.feature.setting.dialog

import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.feature.setting.dialog.GuideViewModel.Event

class GuideViewModel : BaseViewModel<Event>() {

    sealed interface Event : ViewEvent
}
