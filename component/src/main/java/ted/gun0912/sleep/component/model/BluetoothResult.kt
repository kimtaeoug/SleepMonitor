package ted.gun0912.sleep.component.model

import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.SensorInfo

sealed interface BluetoothResult {
    val isConnecting
        get() = this is BluetoothResult

    val isConnected: Boolean
        get() = this is BluetoothResult

    val isRunning: Boolean
        get() = this is BluetoothResult

    val hasBeenDisconnected: Boolean
        get() = when (this) {
            Disconnected, NotSupportUart, NotFound -> true
            Connected, Connecting, is Data, Idle -> false
        }

    object Idle : BluetoothResult
    object Connected : BluetoothResult
    object Connecting : BluetoothResult
    object Disconnected : BluetoothResult
    data class Data(val data: EnvSensorInfo) : BluetoothResult
    object NotFound : BluetoothResult
    object NotSupportUart : BluetoothResult
}
