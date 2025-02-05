package ted.gun0912.sleep.data.local

import ted.gun0912.sleep.model.BluetoothDevice
import java.time.LocalTime

interface ConfigLocalDataResource {

    suspend fun getMyDevice(): BluetoothDevice

    suspend fun getEnvDevice(): BluetoothDevice

    suspend fun saveDevice(bluetoothDevice: BluetoothDevice)

    suspend fun saveEnvDevice(bluetoothDevice: BluetoothDevice)

    suspend fun needShowVitalInfo(): Boolean

    suspend fun showVitalInfo(needShow: Boolean)

    suspend fun needChargePhoneNotice(): Boolean

    suspend fun showChargePhoneNotice(needShow: Boolean)

    suspend fun getLastAlarmTime(): LocalTime

    suspend fun saveAlarmTime(time: LocalTime)

    suspend fun getDevices(): List<BluetoothDevice>

    suspend fun saveMovThr(movthr: String)

    suspend fun saveMotThr(motthr: String)
}
