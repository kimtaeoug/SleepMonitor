package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.ConfigRepository
import javax.inject.Inject

class GetSavedDeviceListUseCase @Inject constructor(private val configRepository: ConfigRepository) {

    operator fun invoke() = configRepository.getSavedDevices()
}
