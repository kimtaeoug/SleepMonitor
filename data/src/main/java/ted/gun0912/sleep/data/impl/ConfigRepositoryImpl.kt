package ted.gun0912.sleep.data.impl

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.data.bound.flowDataResource
import ted.gun0912.sleep.data.local.ConfigLocalDataResource
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.domain.repository.ConfigRepository
import ted.gun0912.sleep.model.BluetoothDevice
import java.time.LocalTime
import javax.inject.Inject

internal class ConfigRepositoryImpl @Inject constructor(
    private val configLocalDataResource: ConfigLocalDataResource,
) : ConfigRepository {

    override fun getMyDevice(): Flow<DataResource<BluetoothDevice>> =
        flowDataResource { configLocalDataResource.getMyDevice() }

    override fun getMyEnvDevice(): Flow<DataResource<BluetoothDevice>> =
        flowDataResource { configLocalDataResource.getEnvDevice() }


    override fun saveMyDevice(bluetoothDevice: BluetoothDevice): Flow<DataResource<Unit>> =
        flowDataResource { configLocalDataResource.saveDevice(bluetoothDevice) }

    override fun saveEnvDevice(bluetoothDevice: BluetoothDevice): Flow<DataResource<Unit>> =
        flowDataResource { configLocalDataResource.saveEnvDevice(bluetoothDevice) }

    override fun needShowVitalInfo(): Flow<DataResource<Boolean>> =
        flowDataResource { configLocalDataResource.needShowVitalInfo() }

    override fun showVitalInfo(needShow: Boolean): Flow<DataResource<Unit>> =
        flowDataResource { configLocalDataResource.showVitalInfo(needShow) }

    override fun needShowChargePhoneNotice(): Flow<DataResource<Boolean>> =
        flowDataResource { configLocalDataResource.needChargePhoneNotice() }

    override fun showChargePhoneNotice(needShow: Boolean): Flow<DataResource<Unit>> =
        flowDataResource { configLocalDataResource.showChargePhoneNotice(needShow) }

    override fun getLastAlarmTime(): Flow<DataResource<LocalTime>> =
        flowDataResource { configLocalDataResource.getLastAlarmTime() }

    override fun saveAlarmTime(time: LocalTime): Flow<DataResource<Unit>> =
        flowDataResource { configLocalDataResource.saveAlarmTime(time) }

    override fun getSavedDevices(): Flow<DataResource<List<BluetoothDevice>>> =
        flowDataResource { configLocalDataResource.getDevices() }

    override fun saveMotThr() {
        TODO("Not yet implemented")
    }

    override fun getMotThr() {
        TODO("Not yet implemented")
    }

    override fun saveMovThr() {
        TODO("Not yet implemented")
    }

    override fun getMovThr() {
        TODO("Not yet implemented")
    }
}
