package ted.gun0912.sleep.ui.feature.onboarding.scan

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.support.v18.scanner.ScanResult
import ted.gun0912.sleep.domain.usecase.GetSavedDeviceListUseCase
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import javax.inject.Inject

@HiltViewModel
class OnBoardingScanViewModel @Inject constructor(
    getSavedDeviceListUseCase: GetSavedDeviceListUseCase
) : BaseViewModel<OnBoardingScanViewModel.Event>() {

    private var savedDevices = emptyList<BluetoothDevice>()

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices = _devices.asStateFlow()

    init {
        launch {
            savedDevices = getSavedDeviceListUseCase().await() ?: return@launch
        }
    }

    fun setScanResults(scanResults: List<ScanResult>) {
        _devices.value = scanResults.map { scanResult ->
            val scanDeviceAddress = scanResult.device.address ?: ""
            val savedDevice = savedDevices.find { it.address == scanDeviceAddress }

            val scanDeviceName = scanResult.scanRecord?.deviceName ?: ""
            val deviceName = savedDevice?.name ?: scanDeviceName
            BluetoothDevice(deviceName, scanDeviceAddress)
        }
    }

    fun selectDevice(bluetoothDevice: BluetoothDevice) {
        clearScanDevices()
        event(Event.SelectDevice(bluetoothDevice))
    }

    private fun clearScanDevices(){
        _devices.value = emptyList()
    }

    sealed interface Event : ViewEvent {
        data class SelectDevice(val bluetoothDevice: BluetoothDevice) : Event
    }

}
