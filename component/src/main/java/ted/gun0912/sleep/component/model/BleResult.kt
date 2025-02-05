package ted.gun0912.sleep.component.model

import ted.gun0912.sleep.model.SensorInfo
import kotlin.math.log

sealed interface BleResult {

    val isConnecting
        get() = this is Connecting

    val isConnected: Boolean
        get() = this is Connected

    val isRunning: Boolean
        get() = this is Data

    val hasBeenDisconnected: Boolean
        get() = when (this) {
            Disconnected, NotSupportUart, NotFound -> true
            Connected, Connecting, is Data, Idle -> false
        }

    object Idle : BleResult
    object Connected : BleResult
    object Connecting : BleResult
    object Disconnected : BleResult
    data class Data(val data: SensorInfo) : BleResult
    object NotFound : BleResult
    object NotSupportUart : BleResult
}
