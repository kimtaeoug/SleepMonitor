package ted.gun0912.sleep.bluetooth.uart

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.WriteRequest
import no.nordicsemi.android.ble.ktx.asFlow
import no.nordicsemi.android.ble.ktx.suspend
import ted.gun0912.sleep.common.util.loge
import ted.gun0912.sleep.common.util.logw
import ted.gun0912.sleep.component.model.BleResult
import ted.gun0912.sleep.model.SensorInfo
import java.util.UUID

internal class UARTManager(
    context: Context,
    private val scope: CoroutineScope,
) : BleManager(context) {


    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null

    private var useLongWrite = true


    private val _bleResult = MutableStateFlow<BleResult>(BleResult.Idle)
    val bleResult = _bleResult.asStateFlow()

    private val data = MutableStateFlow<BleResult.Data?>(null)
    private val dataHolder = ConnectionObserverAdapter()

    init {
        connectionObserver = dataHolder

        dataHolder.connectState.onEach {
            _bleResult.value = it
        }.launchIn(scope)

        data
            .filterNotNull()
            .onEach {
                _bleResult.value = it
            }.launchIn(scope)
    }

    override fun log(priority: Int, message: String) {
        // logd(message)
    }

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    private inner class UARTManagerGattCallback : BleManagerGattCallback() {

        @SuppressLint("WrongConstant")
        override fun initialize() {
            setNotificationCallback(txCharacteristic).asFlow()
                .flowOn(Dispatchers.IO)
                .onEach {
                    val rawText: String = it.getStringValue(0) ?: ""
                    val vitalSensorInfo = getVitalSensorInfo(rawText)
                    if (vitalSensorInfo != null) {
                        _bleResult.value = BleResult.Data(vitalSensorInfo)
                    }

                }.launchIn(scope)

            requestMtu(517).enqueue()
            enableNotifications(txCharacteristic).enqueue()

        }

        private fun getVitalSensorInfo(rawText: String): SensorInfo? =
            runCatching {
                SensorInfo.from(rawText)
            }
                .onFailure {
                    logw("text: $rawText")
                    loge(it.toString())
                }
                .getOrNull()


        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service: BluetoothGattService? = gatt.getService(UART_SERVICE_UUID)
            if (service != null) {
                rxCharacteristic = service.getCharacteristic(UART_RX_CHARACTERISTIC_UUID)
                txCharacteristic = service.getCharacteristic(UART_TX_CHARACTERISTIC_UUID)
            }
            var writeRequest = false
            var writeCommand = false

            rxCharacteristic?.let {
                val rxProperties: Int = it.properties
                writeRequest = rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0
                writeCommand =
                    rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0

                // Set the WRITE REQUEST type when the characteristic supports it.
                // This will allow to send long write (also if the characteristic support it).
                // In case there is no WRITE REQUEST property, this manager will divide texts
                // longer then MTU-3 bytes into up to MTU-3 bytes chunks.
                if (!writeRequest) {
                    useLongWrite = false
                }
            }
            return rxCharacteristic != null && txCharacteristic != null && (writeRequest || writeCommand)
        }

        override fun onServicesInvalidated() {
            rxCharacteristic = null
            txCharacteristic = null
            useLongWrite = true
        }
    }

    @SuppressLint("WrongConstant")
    fun send(text: String) {
        if (rxCharacteristic == null) return
        scope.launch {
            val writeType = if (useLongWrite) {
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            } else {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            val request: WriteRequest =
                writeCharacteristic(rxCharacteristic, text.toByteArray(), writeType)
            if (!useLongWrite) {
                request.split()
            }
            request.suspend()
            log(10, "\"$text\" sent")
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return UARTManagerGattCallback()
    }
}
