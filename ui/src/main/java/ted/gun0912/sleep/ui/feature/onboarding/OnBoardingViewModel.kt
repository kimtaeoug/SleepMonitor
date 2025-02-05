package ted.gun0912.sleep.ui.feature.onboarding

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.domain.usecase.SaveMyDeviceUseCase
import ted.gun0912.sleep.domain.usecase.SaveMyEnvDeviceUseCase
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingViewModel.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveMyDeviceUseCase: SaveMyDeviceUseCase,
    private val saveMyEnvDeviceUseCase: SaveMyEnvDeviceUseCase
) : BaseViewModel<Event>() {

    private val _selectedSleepDevice = MutableStateFlow<BluetoothDevice?>(null)
    val selectedSleepDevice = _selectedSleepDevice

    sealed interface Event : ViewEvent {
    }

    fun saveSleepDeviec(bluetoothDevice: BluetoothDevice) {
        viewModelScope.launch {
            saveMyDeviceUseCase(bluetoothDevice).collectLatest {
                _selectedSleepDevice.value = bluetoothDevice
                // TODO handle error
            }
        }
    }

    fun saveEnvDevice(bluetoothDevice: BluetoothDevice) {
        viewModelScope.launch {
            saveMyEnvDeviceUseCase(bluetoothDevice).collectLatest {
                // TODO handle error
            }
        }
    }
}
