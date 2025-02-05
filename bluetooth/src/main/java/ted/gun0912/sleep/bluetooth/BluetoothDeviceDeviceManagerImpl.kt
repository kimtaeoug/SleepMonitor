package ted.gun0912.sleep.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.suspend
import ted.gun0912.sleep.bluetooth.bluetooth_classic.ConnectionThread
import ted.gun0912.sleep.bluetooth.uart.UARTManager
import ted.gun0912.sleep.bluetooth.uart.UARTService
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.common.util.loge
import ted.gun0912.sleep.component.model.BleResult
import ted.gun0912.sleep.component.model.BluetoothResult
import ted.gun0912.sleep.model.EnvSensorInfo
import javax.inject.Inject

@SuppressLint("MissingPermission")
internal class BluetoothDeviceDeviceManagerImpl @Inject internal constructor(
    @ApplicationContext
    private val context: Context,
) : ted.gun0912.sleep.component.BluetoothDeviceManager {

    private var manager: UARTManager? = null

    private var bluetoothConnectThread: ConnectionThread? = null

    private val _sleepBleResult = MutableStateFlow<BleResult>(BleResult.Idle)
    override val sleepBleResult = _sleepBleResult.asStateFlow()

    private val _envBlutoothResult = MutableStateFlow<BluetoothResult>(BluetoothResult.Idle)
    override val envBlutoothResult = _envBlutoothResult.asStateFlow()

    val isRunning = sleepBleResult.map { it.isRunning }
    val hasBeenDisconnected = sleepBleResult.map { it.hasBeenDisconnected }

    override fun connect(sleepDeviceAddress: String, envDeviceAddress: String) {
        val intent = Intent(context, UARTService::class.java).apply {
            putExtra(UARTService.EXTRA_SLEEP_DEVICE_ADDRESS, sleepDeviceAddress)
            putExtra(UARTService.EXTRA_ENV_DEVICE_ADDRESS, envDeviceAddress)
        }
        context.startService(intent)
    }

    override fun start(deviceAddress: String, envDeviceAddress: String, scope: CoroutineScope) {
        startEnvDevice(envDeviceAddress)
        val manager = UARTManager(context, scope)
        this.manager = manager

        manager.bleResult.onEach {
            _sleepBleResult.value = it
        }.launchIn(scope)
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothDevice = bluetoothManager.adapter.getRemoteDevice(deviceAddress)
        scope.launch {
            manager.start(bluetoothDevice)
        }
    }

    fun startEnvDevice(deviceAddress: String) {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.getAdapter()

        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }

            bluetoothConnectThread?.cancel()
            bluetoothConnectThread = null

            val device = adapter.getRemoteDevice(deviceAddress)
            try {
                bluetoothConnectThread =
                    ConnectionThread(
                        device = device,
                        onDataChange = { it?.let { _envBlutoothResult.value = it } }
                    )
                bluetoothConnectThread?.start()
            } catch (e: Exception) { // 연결에 실패할 경우 호출됨
                loge(e.toString())
                e.printStackTrace()
                _envBlutoothResult.value = BluetoothResult.NotFound
                return
            }
        }
    }

    private suspend fun UARTManager.start(device: BluetoothDevice) {
        try {
            connect(device)//YJH0705 autoConnect
                //.useAutoConnect(true)
                .retry(3)
                .timeout(2000L)
                .suspend()
        } catch (e: Exception) {
            loge(e.toString())
            e.printStackTrace()
            _sleepBleResult.value = BleResult.NotFound
        }
    }

    override fun sendCommand(command: String, scope: CoroutineScope) {
        scope.launch {
            sleepBleResult.map { it.isConnected }.firstOrNull() ?: return@launch
            logd("$command 전송 시작")
            manager?.send(command)
        }
    }

    override fun disconnect() {
        manager?.disconnect()?.enqueue()
        manager = null
    }

    override fun disconnectBluetoothDevice() {
        bluetoothConnectThread?.cancel()
        bluetoothConnectThread = null
    }
}
