package ted.gun0912.sleep.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ted.gun0912.sleep.component.model.BleResult
import ted.gun0912.sleep.component.model.BluetoothResult

interface BluetoothDeviceManager {

    val sleepBleResult: StateFlow<BleResult>
    val envBlutoothResult: StateFlow<BluetoothResult>

    fun connect(sleepDeviceAddress: String, envDeviceAddress: String)

    fun start(deviceAddress: String, envDeviceAddress: String, scope: CoroutineScope)

    fun sendCommand(command: String, scope: CoroutineScope)

    fun disconnect()

    fun disconnectBluetoothDevice()
}
