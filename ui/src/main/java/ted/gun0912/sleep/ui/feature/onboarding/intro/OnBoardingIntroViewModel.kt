package ted.gun0912.sleep.ui.feature.onboarding.intro

import dagger.hilt.android.lifecycle.HiltViewModel
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import javax.inject.Inject

@HiltViewModel
class OnBoardingIntroViewModel @Inject constructor(

) : BaseViewModel<OnBoardingIntroViewModel.Event>() {

    sealed interface Event : ViewEvent {

    }
}
