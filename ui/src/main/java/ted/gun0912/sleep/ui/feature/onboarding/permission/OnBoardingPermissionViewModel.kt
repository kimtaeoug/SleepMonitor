package ted.gun0912.sleep.ui.feature.onboarding.permission

import dagger.hilt.android.lifecycle.HiltViewModel
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import javax.inject.Inject

@HiltViewModel
class OnBoardingPermissionViewModel @Inject constructor(

) : BaseViewModel<OnBoardingPermissionViewModel.Event>() {

    sealed interface Event : ViewEvent {

    }
}
