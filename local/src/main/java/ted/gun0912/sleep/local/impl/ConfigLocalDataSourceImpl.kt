package ted.gun0912.sleep.local.impl

import ted.gun0912.sleep.data.local.ConfigLocalDataResource
import ted.gun0912.sleep.local.pref.PrefUtil
import ted.gun0912.sleep.model.BluetoothDevice
import java.time.LocalTime
import javax.inject.Inject

class ConfigLocalDataSourceImpl @Inject constructor(
    private val prefUtil: PrefUtil,
) : ConfigLocalDataResource {

    override suspend fun getMyDevice(): BluetoothDevice = prefUtil.bluetoothDevice

    override suspend fun getEnvDevice(): BluetoothDevice =  prefUtil.envBluetoothDevice

    override suspend fun saveDevice(bluetoothDevice: BluetoothDevice) {
        prefUtil.bluetoothDevice = bluetoothDevice
    }

    override suspend fun saveEnvDevice(bluetoothDevice: BluetoothDevice) {
        prefUtil.envBluetoothDevice = bluetoothDevice
    }

    override suspend fun needShowVitalInfo(): Boolean = prefUtil.needShowVitalInfo

    override suspend fun showVitalInfo(needShow: Boolean) {
        prefUtil.needShowVitalInfo = needShow
    }

    override suspend fun needChargePhoneNotice(): Boolean = prefUtil.needShowChargePhoneNotice

    override suspend fun showChargePhoneNotice(needShow: Boolean) {
        prefUtil.needShowChargePhoneNotice = needShow
    }

    override suspend fun getLastAlarmTime(): LocalTime = prefUtil.lastAlarmTime

    override suspend fun saveAlarmTime(time: LocalTime) {
        prefUtil.lastAlarmTime = time
    }

    override suspend fun getDevices(): List<BluetoothDevice> = prefUtil.bluetoothDevices

    override suspend fun saveMovThr(movthr: String) {
        prefUtil.movthr = movthr
    }

    override suspend fun saveMotThr(motthr: String) {
        prefUtil.motthr = motthr
    }
}
