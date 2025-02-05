package ted.gun0912.sleep.ui.feature.splash

import dagger.hilt.android.lifecycle.HiltViewModel
import ted.gun0912.sleep.domain.usecase.GetMyDeviceUseCase
import ted.gun0912.sleep.domain.usecase.GetMyEnvDeviceUseCase
import ted.gun0912.sleep.domain.usecase.SaveMyEnvDeviceUseCase
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.feature.splash.SplashViewModel.Event
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    getMyDeviceUseCase: GetMyDeviceUseCase,
    getMyEnvDeviceUseCase: GetMyEnvDeviceUseCase
) :
    BaseViewModel<Event>() {

    init {
        launch {
            val sleepDevice = getMyDeviceUseCase().await() ?: return@launch
            val sleepDeviceAddress = sleepDevice.address

            val envDevice = getMyEnvDeviceUseCase().await() ?: return@launch
            val envDeviceAddress = envDevice.address

            if (sleepDeviceAddress.isBlank() || envDeviceAddress.isBlank()) {
                event(Event.ShowOnboarding)
            } else {
                event(Event.ShowMain(sleepDeviceAddress, envDeviceAddress))
            }
        }
    }

    sealed interface Event : ViewEvent {
        object ShowOnboarding : Event
        data class ShowMain(val sleepDeviceAddress: String, val envDeviceAddress: String) : Event
    }
}
