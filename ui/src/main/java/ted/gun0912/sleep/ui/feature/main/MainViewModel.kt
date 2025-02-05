package ted.gun0912.sleep.ui.feature.main

import dagger.hilt.android.lifecycle.HiltViewModel
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.feature.main.MainViewModel.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<Event>() {


    sealed class Event : ViewEvent {

    }
}
