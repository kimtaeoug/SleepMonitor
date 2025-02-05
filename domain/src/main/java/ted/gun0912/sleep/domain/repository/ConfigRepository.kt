package ted.gun0912.sleep.domain.repository

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.model.BluetoothDevice
import java.time.LocalTime

interface ConfigRepository {

    fun getMyDevice(): Flow<DataResource<BluetoothDevice>>

    fun getMyEnvDevice(): Flow<DataResource<BluetoothDevice>>

    fun saveMyDevice(bluetoothDevice: BluetoothDevice): Flow<DataResource<Unit>>

    fun saveEnvDevice(bluetoothDevice: BluetoothDevice): Flow<DataResource<Unit>>

    fun needShowVitalInfo(): Flow<DataResource<Boolean>>

    fun showVitalInfo(needShow: Boolean): Flow<DataResource<Unit>>

    fun needShowChargePhoneNotice(): Flow<DataResource<Boolean>>

    fun showChargePhoneNotice(needShow: Boolean): Flow<DataResource<Unit>>

    fun getLastAlarmTime(): Flow<DataResource<LocalTime>>

    fun saveAlarmTime(time: LocalTime): Flow<DataResource<Unit>>

    fun getSavedDevices(): Flow<DataResource<List<BluetoothDevice>>>

    fun saveMotThr()

    fun getMotThr()

    fun saveMovThr()

    fun getMovThr()

}
