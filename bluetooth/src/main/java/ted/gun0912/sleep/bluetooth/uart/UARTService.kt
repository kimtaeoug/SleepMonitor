package ted.gun0912.sleep.bluetooth.uart

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ted.gun0912.sleep.bluetooth.NotificationService
import ted.gun0912.sleep.component.BluetoothDeviceManager
import javax.inject.Inject

@AndroidEntryPoint
internal class UARTService : NotificationService() {

    @Inject
    lateinit var bluetoothDeviceManager: BluetoothDeviceManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val sleepDeviceAddress: String =
            intent?.getStringExtra(EXTRA_SLEEP_DEVICE_ADDRESS) ?: return START_REDELIVER_INTENT

        val envDeviceAddress: String =
            intent?.getStringExtra(EXTRA_ENV_DEVICE_ADDRESS) ?: return START_REDELIVER_INTENT

        bluetoothDeviceManager.start(sleepDeviceAddress, envDeviceAddress, lifecycleScope)
        bluetoothDeviceManager.sleepBleResult
            .map { it.hasBeenDisconnected }.onEach {
                if (it) stopSelf()
            }.launchIn(lifecycleScope)

        return START_REDELIVER_INTENT
    }

    companion object {
        const val EXTRA_SLEEP_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS"
        const val EXTRA_ENV_DEVICE_ADDRESS = "EXTRA_ENV_DEVICE_ADDRESS"
    }
}
