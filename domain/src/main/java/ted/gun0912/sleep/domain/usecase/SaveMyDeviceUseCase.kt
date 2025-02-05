package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.ConfigRepository
import ted.gun0912.sleep.model.BluetoothDevice
import javax.inject.Inject

class SaveMyDeviceUseCase @Inject constructor(private val configRepository: ConfigRepository) {

    operator fun invoke(bluetoothDevice: BluetoothDevice) = configRepository.saveMyDevice(bluetoothDevice)
}
