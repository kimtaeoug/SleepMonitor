package ted.gun0912.sleep.bluetooth.uart

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.ble.observer.ConnectionObserver
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.common.util.logw
import ted.gun0912.sleep.component.model.BleResult

class ConnectionObserverAdapter : ConnectionObserver {

    private val _connectState = MutableStateFlow<BleResult>(BleResult.Idle)
    val connectState = _connectState.asStateFlow()

    override fun onDeviceConnecting(device: BluetoothDevice) {
        _connectState.value = BleResult.Connecting
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        _connectState.value = BleResult.Connected
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        logw("NotFound, reason: $reason")
        _connectState.value = BleResult.NotFound
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        logd("onDeviceReady()")
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        logd("onDeviceDisconnected(), reason: $reason")
        _connectState.value = when (reason) {
            ConnectionObserver.REASON_NOT_SUPPORTED -> BleResult.NotSupportUart
            /*
            ConnectionObserver.REASON_LINK_LOSS -> LinkLossResult(device, getData())
            ConnectionObserver.REASON_SUCCESS -> DisconnectedResult(device)
            */
            else -> BleResult.Disconnected
        }
    }
}
